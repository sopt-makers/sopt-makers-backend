package org.sopt.makers.domain.app.soptamp.rank.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.port.SoptampPartMemberCountPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.app.soptamp.rank.PartRank;
import org.sopt.makers.domain.app.soptamp.rank.RankedScore;
import org.sopt.makers.domain.app.soptamp.rank.SoptampPartRankCalculator;
import org.sopt.makers.domain.app.soptamp.rank.SoptampUserRankCalculator;
import org.sopt.makers.domain.app.soptamp.rank.UserRank;
import org.sopt.makers.domain.app.soptamp.rank.port.RankCachePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RankService {

  private final SoptampUserQueryPort soptampUserQueryPort;
  private final SoptampPartMemberCountPort soptampPartMemberCountPort;
  private final RankCachePort rankCachePort;
  private final SoptampMode soptampMode;
  private final Long currentGeneration;

  public RankService(
      SoptampUserQueryPort soptampUserQueryPort,
      SoptampPartMemberCountPort soptampPartMemberCountPort,
      RankCachePort rankCachePort,
      SoptampMode soptampMode,
      @Value("${sopt.current.generation}") Long currentGeneration) {
    this.soptampUserQueryPort = soptampUserQueryPort;
    this.soptampPartMemberCountPort = soptampPartMemberCountPort;
    this.rankCachePort = rankCachePort;
    this.soptampMode = soptampMode;
    this.currentGeneration = currentGeneration;
  }

  public List<UserRank> findCurrentRanks() {
    requireNotAppjamSeason();

    List<RankedScore> cachedScores = rankCachePort.getRanking();
    if (!cachedScores.isEmpty()) {
      return toUserRanks(cachedScores);
    }

    List<SoptampUser> users = soptampUserQueryPort.findAllOfCurrentGeneration();
    rankCachePort.putAll(toRankedScores(users));
    return SoptampUserRankCalculator.calculateRank(users);
  }

  public List<UserRank> findCurrentRanksByPart(Part part) {
    requireNotAppjamSeason();
    Part rankingPart = SoptampPart.of(part).toPart();

    List<RankedScore> cachedScores = rankCachePort.getRanking();
    if (!cachedScores.isEmpty()) {
      return toUserRanksOfPart(cachedScores, rankingPart);
    }

    return SoptampUserRankCalculator.calculateRank(
        soptampUserQueryPort.findAllByPartAndCurrentGeneration(rankingPart));
  }

  public List<PartRank> findAllPartRanks() {
    requireNotAppjamSeason();

    List<SoptampUser> users = soptampUserQueryPort.findAllOfCurrentGeneration();
    Map<Part, Long> partMemberCounts =
        soptampPartMemberCountPort.countMembersByPart(currentGeneration.intValue());
    return SoptampPartRankCalculator.calculatePartRank(users, partMemberCounts);
  }

  public void reloadRankCache() {
    if (soptampMode.isAppjam()) {
      return;
    }

    List<SoptampUser> users = soptampUserQueryPort.findAllOfCurrentGeneration();
    rankCachePort.clearScores();
    rankCachePort.putAll(toRankedScores(users));
  }

  private void requireNotAppjamSeason() {
    if (soptampMode.isAppjam()) {
      throw new SoptampException(SoptampFailure.INVALID_APPJAM_SEASON_REQUEST);
    }
  }

  private List<UserRank> toUserRanks(List<RankedScore> cachedScores) {
    Map<Long, SoptampUser> users = findUsersOf(cachedScores);
    AtomicInteger rankPoint = new AtomicInteger(1);

    return cachedScores.stream()
        .filter(score -> hasUserRow(users, score.userId()))
        .map(score -> toUserRank(rankPoint.getAndIncrement(), score, users))
        .toList();
  }

  private List<UserRank> toUserRanksOfPart(List<RankedScore> cachedScores, Part part) {
    Map<Long, SoptampUser> users = findUsersOf(cachedScores);
    AtomicInteger rankPoint = new AtomicInteger(1);

    return cachedScores.stream()
        .filter(score -> hasUserRow(users, score.userId()))
        .filter(score -> rankingPartOf(users.get(score.userId())) == part)
        .map(score -> toUserRank(rankPoint.getAndIncrement(), score, users))
        .toList();
  }

  private static Part rankingPartOf(SoptampUser user) {
    return user.part() == null ? null : user.part().toPart();
  }

  private Map<Long, SoptampUser> findUsersOf(List<RankedScore> cachedScores) {
    return soptampUserQueryPort.findByUserIdsAsMap(
        cachedScores.stream().map(RankedScore::userId).toList());
  }

  private UserRank toUserRank(int rank, RankedScore score, Map<Long, SoptampUser> users) {
    SoptampUser user = users.get(score.userId());
    return new UserRank(rank, user.nickname(), score.score(), user.profileMessage());
  }

  private boolean hasUserRow(Map<Long, SoptampUser> users, Long userId) {
    if (users.containsKey(userId)) {
      return true;
    }
    log.warn("솝탬프 랭킹 캐시에 점수만 남은 유저를 건너뛴다. userId={}", userId);
    return false;
  }

  private List<RankedScore> toRankedScores(List<SoptampUser> users) {
    return users.stream().map(user -> new RankedScore(user.userId(), user.totalPoints())).toList();
  }
}
