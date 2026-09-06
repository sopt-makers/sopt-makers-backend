package org.sopt.makers.domain.playground.member.profile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.profile.exception.UserProfileException;
import org.sopt.makers.domain.playground.member.profile.exception.UserProfileFailure;
import org.sopt.makers.domain.playground.member.profile.port.MemberProfileCardCachePort;
import org.sopt.makers.domain.playground.member.profile.port.MemberProfileRankingCachePort;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.UserFavor;
import org.sopt.makers.domain.user.UserLink;
import org.sopt.makers.domain.user.WorkPreference;
import org.sopt.makers.domain.user.command.ActivityUpdateCommand;
import org.sopt.makers.domain.user.enums.CommunicationStyle;
import org.sopt.makers.domain.user.enums.FeedbackStyle;
import org.sopt.makers.domain.user.enums.IdeationStyle;
import org.sopt.makers.domain.user.enums.WorkPlace;
import org.sopt.makers.domain.user.enums.WorkTime;
import org.sopt.makers.domain.user.port.PlaygroundProfileUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class UserProfileCommandService {

  private final PlaygroundProfileUserPort playgroundProfileUserPort;
  private final MemberProfileRankingCachePort rankingCachePort;
  private final MemberProfileCardCachePort cardCachePort;

  public record ActivityInput(Integer generation, String team) {}

  public record LinkInput(Long id, String title, String url) {}

  public record CareerInput(
      String companyName, String title, String startDate, String endDate, Boolean isCurrent) {}

  public record UserFavorInput(
      Boolean isPourSauceLover,
      Boolean isHardPeachLover,
      Boolean isMintChocoLover,
      Boolean isRedBeanFishBreadLover,
      Boolean isSojuLover,
      Boolean isRiceTteokLover) {}

  public record WorkPreferenceInput(
      String ideationStyle,
      String workTime,
      String communicationStyle,
      String workPlace,
      String feedbackStyle) {}

  public User saveProfile(
      Long userId,
      String email,
      String phone,
      String profileImage,
      List<ActivityInput> activities,
      String address,
      String university,
      String major,
      String introduction,
      String skill,
      String mbti,
      String mbtiDescription,
      Double sojuCapacity,
      String interest,
      UserFavorInput userFavor,
      String idealType,
      String selfIntroduction,
      List<LinkInput> links,
      List<CareerInput> careers,
      Boolean allowOfficial,
      Boolean isPhoneBlind) {
    validateNoMultipleCurrentCareers(careers);

    User current = playgroundProfileUserPort.getUserWithActivities(userId);
    List<ActivityUpdateCommand> activityUpdates = buildActivityUpdates(current, activities);

    playgroundProfileUserPort.updateProfile(
        userId,
        email,
        phone,
        profileImage,
        activityUpdates,
        address,
        university,
        major,
        introduction,
        skill,
        mbti,
        mbtiDescription,
        sojuCapacity,
        interest,
        toUserFavor(userFavor),
        idealType,
        selfIntroduction,
        allowOfficial,
        isPhoneBlind,
        null,
        toLinks(userId, links),
        toCareers(userId, careers));

    playgroundProfileUserPort.completeFirstLogin(userId);
    evictProfileListCaches(userId);

    return playgroundProfileUserPort.getUser(userId);
  }

  public User updateProfile(
      Long userId,
      String email,
      String phone,
      String profileImage,
      List<ActivityInput> activities,
      String address,
      String university,
      String major,
      String introduction,
      String skill,
      String mbti,
      String mbtiDescription,
      Double sojuCapacity,
      String interest,
      UserFavorInput userFavor,
      String idealType,
      String selfIntroduction,
      WorkPreferenceInput workPreference,
      List<LinkInput> links,
      List<CareerInput> careers,
      Boolean allowOfficial,
      Boolean isPhoneBlind) {
    validateNoMultipleCurrentCareers(careers);

    User current = playgroundProfileUserPort.getUserWithActivities(userId);
    List<ActivityUpdateCommand> activityUpdates = buildActivityUpdates(current, activities);

    playgroundProfileUserPort.updateProfile(
        userId,
        email,
        phone,
        profileImage,
        activityUpdates,
        address,
        university,
        major,
        introduction,
        skill,
        mbti,
        mbtiDescription,
        sojuCapacity,
        interest,
        toUserFavor(userFavor),
        idealType,
        selfIntroduction,
        allowOfficial,
        isPhoneBlind,
        toWorkPreference(workPreference),
        toLinksWithId(userId, links),
        toCareers(userId, careers));
    evictProfileListCaches(userId);

    return playgroundProfileUserPort.getUser(userId);
  }

  public void updateWorkPreference(Long userId, WorkPreferenceInput request) {
    playgroundProfileUserPort.upsertWorkPreference(userId, toWorkPreference(request));
  }

  public void deleteLink(Long userId, Long linkId) {
    UserLink link =
        playgroundProfileUserPort
            .findLinkById(linkId)
            .filter(l -> l.userId().equals(userId))
            .orElseThrow(() -> new UserProfileException(UserProfileFailure.NOT_FOUND_LINK));
    playgroundProfileUserPort.deleteLinkById(link.id());
    runAfterCommit(() -> cardCachePort.evict(userId));
  }

  /** editActivitiesAble은 활동 이력 유무로 파생되는 값이라 별도 상태 변경이 없다 — 유저 존재 여부만 검증한다. */
  public void checkActivity(Long userId, Boolean isCheck) {
    playgroundProfileUserPort.getUser(userId);
  }

  /** 랭킹/카드 캐시 무효화는 트랜잭션이 실제로 커밋된 이후에만 실행한다 — 롤백 시 캐시가 잘못 비워지는 것을 방지한다. */
  private void evictProfileListCaches(Long userId) {
    runAfterCommit(
        () -> {
          rankingCachePort.evictTopRanking();
          cardCachePort.evict(userId);
        });
  }

  private void runAfterCommit(Runnable action) {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              action.run();
            }
          });
    } else {
      action.run();
    }
  }

  private void validateNoMultipleCurrentCareers(List<CareerInput> careers) {
    if (careers == null) {
      return;
    }
    long currentCount = careers.stream().filter(c -> Boolean.TRUE.equals(c.isCurrent())).count();
    if (currentCount > 1) {
      throw new UserProfileException(UserProfileFailure.MULTIPLE_CURRENT_CAREERS);
    }
  }

  /**
   * 레거시 team 문자열 왕복 변환(isExecutivePosition/convertTeamToOriginalValue)은 새 모델에서 Activity.role()이
   * 별도 필드로 분리되며 불필요해졌다. MEMBER가 아닌 활동(임원진 등)은 팀 변경 대상에서 제외한다.
   */
  private List<ActivityUpdateCommand> buildActivityUpdates(User current, List<ActivityInput> activities) {
    if (activities == null || activities.isEmpty()) {
      return List.of();
    }

    Map<Integer, Activity> byGeneration =
        current.activities().activities().stream()
            .collect(Collectors.toMap(Activity::generation, Function.identity(), (a, b) -> a));

    List<ActivityUpdateCommand> commands = new ArrayList<>();
    for (ActivityInput input : activities) {
      Activity matched = byGeneration.get(input.generation());
      if (matched == null) {
        throw new UserProfileException(UserProfileFailure.ACTIVITY_GENERATION_MISMATCH);
      }
      if (matched.role() != Role.MEMBER) {
        continue;
      }
      Team team = parseTeam(input.team());
      commands.add(new ActivityUpdateCommand(matched.id(), team));
    }
    return commands;
  }

  private Team parseTeam(String rawTeam) {
    if (rawTeam == null || rawTeam.isBlank() || "해당 없음".equals(rawTeam)) {
      return null;
    }
    for (Team team : Team.values()) {
      if (team.getDisplayName().equals(rawTeam)) {
        return team;
      }
    }
    throw new UserProfileException(UserProfileFailure.INVALID_ACTIVITY_TEAM);
  }

  private UserFavor toUserFavor(UserFavorInput input) {
    if (input == null) {
      return null;
    }
    return UserFavor.of(
        input.isPourSauceLover(),
        input.isHardPeachLover(),
        input.isMintChocoLover(),
        input.isRedBeanFishBreadLover(),
        input.isSojuLover(),
        input.isRiceTteokLover());
  }

  private WorkPreference toWorkPreference(WorkPreferenceInput input) {
    if (input == null) {
      return null;
    }
    return WorkPreference.of(
        IdeationStyle.fromValue(input.ideationStyle()),
        WorkTime.fromValue(input.workTime()),
        CommunicationStyle.fromValue(input.communicationStyle()),
        WorkPlace.fromValue(input.workPlace()),
        FeedbackStyle.fromValue(input.feedbackStyle()));
  }

  private List<UserLink> toLinks(Long userId, List<LinkInput> links) {
    if (links == null) {
      return List.of();
    }
    return links.stream().map(l -> UserLink.of(null, userId, l.title(), l.url())).toList();
  }

  private List<UserLink> toLinksWithId(Long userId, List<LinkInput> links) {
    if (links == null) {
      return List.of();
    }
    return links.stream().map(l -> UserLink.of(l.id(), userId, l.title(), l.url())).toList();
  }

  private List<UserCareer> toCareers(Long userId, List<CareerInput> careers) {
    if (careers == null) {
      return List.of();
    }
    return careers.stream()
        .map(
            c ->
                UserCareer.of(
                    null, userId, c.companyName(), c.title(), c.startDate(), c.endDate(), c.isCurrent()))
        .toList();
  }
}
