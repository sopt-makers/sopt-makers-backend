package org.sopt.makers.domain.app.soptamp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.port.AppjamUserRepositoryPort;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserCommandPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.app.soptamp.rank.SoptampScoreCacheSyncEvent;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SoptampUserUpserter {

  private static final String SUFFIX_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final int MAX_NUMERIC_SUFFIX = 9999;

  private final SoptampUserQueryPort soptampUserQueryPort;
  private final SoptampUserCommandPort soptampUserCommandPort;
  private final AppjamUserRepositoryPort appjamUserRepositoryPort;
  private final SoptampMode soptampMode;
  private final ApplicationEventPublisher eventPublisher;
  private final Long currentGeneration;

  public SoptampUserUpserter(
      SoptampUserQueryPort soptampUserQueryPort,
      SoptampUserCommandPort soptampUserCommandPort,
      AppjamUserRepositoryPort appjamUserRepositoryPort,
      SoptampMode soptampMode,
      ApplicationEventPublisher eventPublisher,
      @Value("${sopt.current.generation}") Long currentGeneration) {
    this.soptampUserQueryPort = soptampUserQueryPort;
    this.soptampUserCommandPort = soptampUserCommandPort;
    this.appjamUserRepositoryPort = appjamUserRepositoryPort;
    this.soptampMode = soptampMode;
    this.eventPublisher = eventPublisher;
    this.currentGeneration = currentGeneration;
  }

  @Transactional
  public List<Long> upsertChunk(List<User> profiles) {
    if (profiles.isEmpty()) {
      return List.of();
    }

    List<Long> userIds = profiles.stream().map(User::id).toList();
    Map<Long, SoptampUser> registeredUsers = soptampUserQueryPort.findByUserIdsAsMap(userIds);
    Map<Long, AppjamUser> appjamUsers = findAppjamUsers(userIds);
    Set<String> reservedNicknames = new HashSet<>();

    List<Long> skippedUserIds = new ArrayList<>();
    for (User profile : profiles) {
      if (upsert(profile, registeredUsers, appjamUsers, reservedNicknames)) {
        skippedUserIds.add(profile.id());
      }
    }
    return skippedUserIds;
  }

  private boolean upsert(
      User profile,
      Map<Long, SoptampUser> registeredUsers,
      Map<Long, AppjamUser> appjamUsers,
      Set<String> reservedNicknames) {
    ActivityList activities = profile.activities();
    Optional<Activity> latestActivity = findLatestActivity(activities);
    if (latestActivity.isEmpty()) {
      return false;
    }

    Activity latest = latestActivity.get();
    long generation = lastGeneration(activities);
    SoptampPart part = SoptampPart.of(latest.part(), latest.role(), latest.team());
    SoptampUser registeredUser = registeredUsers.get(profile.id());

    if (soptampMode.isAppjam()) {
      upsertForAppjam(profile, registeredUser, generation, part, appjamUsers, reservedNicknames);
      return false;
    }
    return upsertForNormalSeason(profile, registeredUser, generation, part, reservedNicknames);
  }

  private boolean upsertForNormalSeason(
      User profile,
      SoptampUser registeredUser,
      long generation,
      SoptampPart part,
      Set<String> reservedNicknames) {
    if (registeredUser != null) {
      if (registeredUser.generation() == null) {
        log.error("솝탬프 유저의 기수가 비어 있어 갱신을 건너뛴다. userId={}", profile.id());
        return true;
      }
      if (Objects.equals(registeredUser.generation(), generation)) {
        return false;
      }
    }

    String baseNickname = part.getShortName() + profile.profile().name();
    boolean isNewUser = registeredUser == null;
    String nickname =
        generateUniqueNickname(baseNickname, isNewUser ? null : profile.id(), reservedNicknames);

    if (isNewUser) {
      soptampUserCommandPort.create(profile.id(), nickname, generation, part);
    } else {
      soptampUserCommandPort.updateChangedGenerationInfo(profile.id(), generation, part, nickname);
    }
    raiseScoreCacheSyncEvent(profile.id(), generation);
    return false;
  }

  private void upsertForAppjam(
      User profile,
      SoptampUser registeredUser,
      long generation,
      SoptampPart part,
      Map<Long, AppjamUser> appjamUsers,
      Set<String> reservedNicknames) {
    String baseNickname = appjamBaseNickname(profile, generation, appjamUsers);

    if (registeredUser == null) {
      soptampUserCommandPort.create(
          profile.id(),
          generateUniqueNickname(baseNickname, null, reservedNicknames),
          generation,
          part);
      return;
    }

    // 이미 앱잼 규칙이 적용된 닉네임이면 그대로 둠 (비트OOO, 37기OOO 등)
    if (!needsAppjamNicknameMigration(registeredUser)) {
      return;
    }

    String nickname = generateUniqueNickname(baseNickname, profile.id(), reservedNicknames);
    // 닉네임이 실제로 바뀌지 않으면 포인트 초기화 없이 종료 (멱등성 보장)
    if (nickname.equals(registeredUser.nickname())) {
      return;
    }
    soptampUserCommandPort.updateChangedGenerationInfo(profile.id(), generation, part, nickname);
  }

  private static String appjamBaseNickname(
      User profile, long generation, Map<Long, AppjamUser> appjamUsers) {
    AppjamUser appjamUser = appjamUsers.get(profile.id());
    String name = profile.profile().name();
    return appjamUser == null ? generation + "기" + name : appjamUser.teamName() + name;
  }

  private static boolean needsAppjamNicknameMigration(SoptampUser registeredUser) {
    String nickname = registeredUser.nickname();
    if (nickname == null || nickname.isBlank()) {
      return true;
    }
    return Arrays.stream(SoptampPart.values())
        .filter(part -> part.toPart() != null)
        .anyMatch(part -> nickname.startsWith(part.getShortName()));
  }

  private Optional<Activity> findLatestActivity(ActivityList activities) {
    if (activities == null || activities.getTotalActivitySize() == 0) {
      return Optional.empty();
    }
    if (soptampMode.isAppjam()) {
      return activities.activities().stream()
          .max(
              Comparator.comparingInt(Activity::generation)
                  .thenComparing(Activity::isSopt, Boolean::compare));
    }
    return activities.activities().stream().anyMatch(Activity::isSopt)
        ? Optional.of(activities.getLastSoptActivity())
        : Optional.empty();
  }

  private static long lastGeneration(ActivityList activities) {
    return activities.activities().stream()
        .filter(Activity::isSopt)
        .mapToInt(Activity::generation)
        .max()
        .orElseGet(
            () -> activities.activities().stream().mapToInt(Activity::generation).max().orElse(0));
  }

  private Map<Long, AppjamUser> findAppjamUsers(List<Long> userIds) {
    if (!soptampMode.isAppjam()) {
      return Map.of();
    }
    return appjamUserRepositoryPort.findAllByUserIdIn(userIds).stream()
        .collect(Collectors.toMap(AppjamUser::userId, Function.identity(), (a, b) -> a));
  }

  private String generateUniqueNickname(
      String baseNickname, Long ownerUserId, Set<String> reservedNicknames) {
    if (isAvailable(baseNickname, ownerUserId, reservedNicknames)) {
      return reserve(baseNickname, reservedNicknames);
    }
    for (int i = 0; i < SUFFIX_CHARS.length(); i++) {
      String candidate = baseNickname + SUFFIX_CHARS.charAt(i);
      if (isAvailable(candidate, ownerUserId, reservedNicknames)) {
        return reserve(candidate, reservedNicknames);
      }
    }
    for (int number = 1; number <= MAX_NUMERIC_SUFFIX; number++) {
      String candidate = baseNickname + number;
      if (isAvailable(candidate, ownerUserId, reservedNicknames)) {
        return reserve(candidate, reservedNicknames);
      }
    }
    throw new SoptampException(SoptampFailure.NICKNAME_IS_FULL);
  }

  private boolean isAvailable(String nickname, Long ownerUserId, Set<String> reservedNicknames) {
    if (reservedNicknames.contains(nickname)) {
      return false;
    }
    return ownerUserId == null
        ? !soptampUserQueryPort.existsByNickname(nickname)
        : !soptampUserQueryPort.existsByNicknameAndUserIdNot(nickname, ownerUserId);
  }

  private static String reserve(String nickname, Set<String> reservedNicknames) {
    reservedNicknames.add(nickname);
    return nickname;
  }

  private void raiseScoreCacheSyncEvent(Long userId, long generation) {
    if (currentGeneration.equals(generation)) {
      eventPublisher.publishEvent(new SoptampScoreCacheSyncEvent(userId));
    }
  }
}
