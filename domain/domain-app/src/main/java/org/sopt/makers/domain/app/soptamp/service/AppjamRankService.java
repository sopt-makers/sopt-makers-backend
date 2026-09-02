package org.sopt.makers.domain.app.soptamp.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayRankSource;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.port.AppjamUserRepositoryPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampRepositoryPort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppjamRankService {

  private final StampRepositoryPort stampRepositoryPort;
  private final AppjamUserRepositoryPort appjamUserRepositoryPort;
  private final SoptampUserQueryPort soptampUserQueryPort;

  public record RankAggregate(
      List<Stamp> latestStamps,
      List<Long> uploaderUserIds,
      Map<Long, AppjamUser> uploaderAppjamUserByUserId,
      Map<Long, SoptampUser> uploaderSoptampUserByUserId) {

    public static RankAggregate empty() {
      return new RankAggregate(List.of(), List.of(), Map.of(), Map.of());
    }
  }

  public RankAggregate findRecentTeamRanks(Pageable pageable) {
    List<Stamp> latestStamps = stampRepositoryPort.findDisplayedLatestStamps(pageable);
    if (latestStamps.isEmpty()) {
      return RankAggregate.empty();
    }

    List<Long> uploaderUserIds = latestStamps.stream().map(Stamp::userId).distinct().toList();
    Map<Long, AppjamUser> uploaderAppjamUserByUserId =
        appjamUserRepositoryPort.findAllByUserIdIn(uploaderUserIds).stream()
            .collect(
                Collectors.toMap(
                    AppjamUser::userId, Function.identity(), (existing, replacement) -> existing));
    Map<Long, SoptampUser> uploaderSoptampUserByUserId =
        soptampUserQueryPort.findAllByUserIds(uploaderUserIds).stream()
            .collect(
                Collectors.toMap(
                    SoptampUser::userId, Function.identity(), (existing, replacement) -> existing));

    return new RankAggregate(
        latestStamps, uploaderUserIds, uploaderAppjamUserByUserId, uploaderSoptampUserByUserId);
  }

  public List<AppjamTodayRankSource> findTodayUserRankSources(
      LocalDateTime todayStart, LocalDateTime tomorrowStart) {
    return stampRepositoryPort.findTodayUserRankSources(todayStart, tomorrowStart);
  }

  public List<AppjamUser> findAllAppjamUsers() {
    return appjamUserRepositoryPort.findAll();
  }

  public Map<Long, Long> findTotalPointsByUserIds(Collection<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Map.of();
    }
    return soptampUserQueryPort.findAllByUserIds(userIds).stream()
        .collect(
            Collectors.toMap(
                SoptampUser::userId,
                user -> user.totalPoints() == null ? 0L : user.totalPoints(),
                (existing, replacement) -> existing));
  }

  public Optional<AppjamUser> findAppjamUserByUserId(Long userId) {
    return appjamUserRepositoryPort.findByUserId(userId);
  }
}
