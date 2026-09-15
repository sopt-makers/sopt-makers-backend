package org.sopt.makers.domain.app.soptamp.facade;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamRecentRankList;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTeamSortType;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayTeamRankList;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayUserRank;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.service.AppjamRankCalculator;
import org.sopt.makers.domain.app.soptamp.service.AppjamRankService;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.SoptampUserPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AppjamRankFacade {

  private static final ZoneId KST = ZoneId.of("Asia/Seoul");

  private final SoptampUserPort soptampUserPort;
  private final AppjamRankService appjamRankService;
  private final Clock clock;

  @Transactional(readOnly = true)
  public AppjamRecentRankList findRecentTeamRanks(int size) {
    Pageable pageable = PageRequest.of(0, size);
    AppjamRankService.RankAggregate aggregate = appjamRankService.findRecentTeamRanks(pageable);
    if (aggregate.latestStamps().isEmpty()) {
      return AppjamRecentRankList.of(List.of());
    }

    Map<Long, User> userById =
        soptampUserPort.findAllWithActivitiesByIds(aggregate.uploaderUserIds()).stream()
            .collect(
                Collectors.toMap(
                    User::id, Function.identity(), (existing, replacement) -> existing));

    AppjamRankCalculator calculator =
        new AppjamRankCalculator(
            aggregate.latestStamps(),
            aggregate.uploaderAppjamUserByUserId(),
            aggregate.uploaderSoptampUserByUserId(),
            userById);
    return AppjamRecentRankList.of(calculator.calculateRecentTeamRanks(size));
  }

  @Transactional(readOnly = true)
  public AppjamTodayTeamRankList findTodayTeamRanks(int size, AppjamTeamSortType sort) {
    LocalDateTime todayStart = LocalDate.now(clock.withZone(KST)).atStartOfDay();
    LocalDateTime tomorrowStart = todayStart.plusDays(1);

    List<AppjamTodayUserRank> todayUserRanks = findTodayUserRanks(todayStart, tomorrowStart);
    List<AppjamUser> allAppjamUsers =
        appjamRankService.findAllAppjamUsers().stream()
            .filter(user -> user.teamNumber() != null)
            .toList();
    List<Long> appjamUserIds = allAppjamUsers.stream().map(AppjamUser::userId).distinct().toList();
    Map<Long, Long> totalPointsByUserId = appjamRankService.findTotalPointsByUserIds(appjamUserIds);
    int teamCount = (int) allAppjamUsers.stream().map(AppjamUser::teamNumber).distinct().count();
    int effectiveSize = Math.max(Math.max(size, 1), teamCount);

    AppjamRankCalculator calculator =
        new AppjamRankCalculator(List.of(), Map.of(), Map.of(), Map.of());
    AppjamTodayTeamRankList result =
        calculator.calculateTodayTeamRanks(
            todayUserRanks, totalPointsByUserId, allAppjamUsers, effectiveSize);

    if (sort == AppjamTeamSortType.NAME) {
      return AppjamTodayTeamRankList.of(
          result.ranks().stream().sorted(Comparator.comparing(rank -> rank.teamName())).toList());
    }
    return result;
  }

  @Transactional(readOnly = true)
  public Integer findMyTeamRank(Long userId) {
    AppjamUser myAppjamUser = appjamRankService.findAppjamUserByUserId(userId).orElse(null);
    if (myAppjamUser == null || myAppjamUser.teamNumber() == null) {
      return null;
    }

    TeamNumber myTeamNumber = myAppjamUser.teamNumber();
    AppjamTodayTeamRankList ranks = findTodayTeamRanks(0, AppjamTeamSortType.SCORE);
    return ranks.ranks().stream()
        .filter(rank -> rank.teamNumber() == myTeamNumber)
        .map(rank -> rank.rank())
        .findFirst()
        .orElse(null);
  }

  private List<AppjamTodayUserRank> findTodayUserRanks(
      LocalDateTime todayStart, LocalDateTime tomorrowStart) {
    return appjamRankService.findTodayUserRankSources(todayStart, tomorrowStart).stream()
        .map(
            source ->
                new AppjamTodayUserRank(
                    source.userId(), source.todayPoints(), source.firstCertifiedAtToday()))
        .toList();
  }
}
