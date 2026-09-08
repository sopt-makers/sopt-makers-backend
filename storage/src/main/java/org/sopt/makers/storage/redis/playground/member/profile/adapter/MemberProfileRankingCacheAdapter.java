package org.sopt.makers.storage.redis.playground.member.profile.adapter;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.playground.member.profile.UserProfileRanking;
import org.sopt.makers.domain.playground.member.profile.port.UserProfileRankingCachePort;
import org.sopt.makers.storage.redis.playground.member.profile.cache.CachedMemberProfileRanking;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/** Tier 1 캐시 어댑터. Redis 장애/역직렬화 실패 시 조용히 미스로 취급해 호출부가 DB 경로로 폴백하게 한다. */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberProfileRankingCacheAdapter implements UserProfileRankingCachePort {

  private static final String KEY = "members:top50:ids";
  private static final Duration TTL = Duration.ofMinutes(10);

  private final RedisTemplate<String, CachedMemberProfileRanking> memberProfileRankingRedisTemplate;

  @Override
  public Optional<UserProfileRanking> getTopRanking() {
    try {
      CachedMemberProfileRanking cached = memberProfileRankingRedisTemplate.opsForValue().get(KEY);
      return Optional.ofNullable(cached)
          .map(c -> new UserProfileRanking(c.topUserIds(), c.totalCount()));
    } catch (Exception exception) {
      log.warn("멤버 프로필 랭킹 캐시 조회 실패", exception);
      return Optional.empty();
    }
  }

  @Override
  public void putTopRanking(UserProfileRanking ranking) {
    try {
      memberProfileRankingRedisTemplate
          .opsForValue()
          .set(
              KEY, new CachedMemberProfileRanking(ranking.topUserIds(), ranking.totalCount()), TTL);
    } catch (Exception exception) {
      log.warn("멤버 프로필 랭킹 캐시 저장 실패", exception);
    }
  }

  @Override
  public void evictTopRanking() {
    try {
      memberProfileRankingRedisTemplate.delete(KEY);
    } catch (Exception exception) {
      log.warn("멤버 프로필 랭킹 캐시 삭제 실패", exception);
    }
  }
}
