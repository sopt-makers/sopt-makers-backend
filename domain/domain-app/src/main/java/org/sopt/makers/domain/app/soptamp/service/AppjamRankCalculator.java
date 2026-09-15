package org.sopt.makers.domain.app.soptamp.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamRecentRank;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayTeamRank;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayTeamRankList;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayUserRank;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.user.User;

@RequiredArgsConstructor
public class AppjamRankCalculator {

  private final List<Stamp> latestStamps;
  private final Map<Long, AppjamUser> uploaderAppjamUserByUserId;
  private final Map<Long, SoptampUser> uploaderSoptampUserByUserId;
  private final Map<Long, User> userById;

  public List<AppjamRecentRank> calculateRecentTeamRanks(int size) {
    return latestStamps.stream()
        .map(this::toRecentRank)
        .filter(Objects::nonNull)
        .limit(size)
        .toList();
  }

  public AppjamTodayTeamRankList calculateTodayTeamRanks(
      List<AppjamTodayUserRank> todayUserRanks,
      Map<Long, Long> totalPointsByUserId,
      List<AppjamUser> allAppjamUsers,
      int size) {
    Map<Long, AppjamTodayUserRank> todayRankByUserId =
        todayUserRanks.stream()
            .collect(
                Collectors.toMap(
                    AppjamTodayUserRank::userId,
                    Function.identity(),
                    (existing, replacement) -> existing));
    Map<TeamNumber, String> teamNameByTeamNumber =
        allAppjamUsers.stream()
            .collect(
                Collectors.toMap(
                    AppjamUser::teamNumber,
                    AppjamUser::teamName,
                    (existing, replacement) -> existing));
    Map<TeamNumber, List<AppjamUser>> membersByTeamNumber =
        allAppjamUsers.stream().collect(Collectors.groupingBy(AppjamUser::teamNumber));

    List<TeamAggregate> teamAggregates =
        membersByTeamNumber.entrySet().stream()
            .map(
                entry ->
                    aggregateTeam(
                        entry.getKey(),
                        entry.getValue(),
                        teamNameByTeamNumber.getOrDefault(entry.getKey(), ""),
                        todayRankByUserId,
                        totalPointsByUserId))
            .sorted(
                Comparator.comparingLong(TeamAggregate::todayPoints)
                    .reversed()
                    .thenComparing(
                        TeamAggregate::firstCertifiedAtToday,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(TeamAggregate::teamNumber))
            .limit(size)
            .toList();

    AtomicInteger rankCounter = new AtomicInteger(1);
    List<AppjamTodayTeamRank> ranks =
        teamAggregates.stream()
            .map(
                teamAggregate ->
                    new AppjamTodayTeamRank(
                        rankCounter.getAndIncrement(),
                        teamAggregate.teamNumber(),
                        teamAggregate.teamName(),
                        teamAggregate.todayPoints(),
                        teamAggregate.totalPoints()))
            .toList();

    return AppjamTodayTeamRankList.of(ranks);
  }

  private AppjamRecentRank toRecentRank(Stamp stamp) {
    AppjamUser uploaderAppjamUser = uploaderAppjamUserByUserId.get(stamp.userId());
    SoptampUser uploaderSoptampUser = uploaderSoptampUserByUserId.get(stamp.userId());
    User user = userById.get(stamp.userId());
    if (uploaderAppjamUser == null || uploaderSoptampUser == null || user == null) {
      return null;
    }

    String firstImageUrl =
        Optional.ofNullable(stamp.images())
            .filter(images -> !images.isEmpty())
            .map(List::getFirst)
            .orElse("");

    return new AppjamRecentRank(
        stamp.id(),
        stamp.missionId(),
        stamp.userId(),
        firstImageUrl,
        stamp.createdAt(),
        uploaderSoptampUser.nickname(),
        user.profile().name(),
        Optional.ofNullable(user.profile().profileImage()).orElse(""),
        uploaderAppjamUser.teamName(),
        uploaderAppjamUser.teamNumber());
  }

  private TeamAggregate aggregateTeam(
      TeamNumber teamNumber,
      List<AppjamUser> teamMembers,
      String teamName,
      Map<Long, AppjamTodayUserRank> todayRankByUserId,
      Map<Long, Long> totalPointsByUserId) {
    long todayPointsSum = 0L;
    long totalPointsSum = 0L;
    LocalDateTime firstCertifiedAtToday = null;

    for (AppjamUser teamMember : teamMembers) {
      Long userId = teamMember.userId();
      AppjamTodayUserRank todayRank = todayRankByUserId.get(userId);
      if (todayRank != null) {
        todayPointsSum += todayRank.todayPoints();
        LocalDateTime certifiedAt = todayRank.firstCertifiedAtToday();
        if (certifiedAt != null
            && (firstCertifiedAtToday == null || certifiedAt.isBefore(firstCertifiedAtToday))) {
          firstCertifiedAtToday = certifiedAt;
        }
      }
      totalPointsSum += totalPointsByUserId.getOrDefault(userId, 0L);
    }

    return new TeamAggregate(
        teamNumber, teamName, todayPointsSum, totalPointsSum, firstCertifiedAtToday);
  }

  private record TeamAggregate(
      TeamNumber teamNumber,
      String teamName,
      long todayPoints,
      long totalPoints,
      LocalDateTime firstCertifiedAtToday) {}
}
