package org.sopt.makers.domain.playground.member.profile.port;

import java.util.Optional;
import org.sopt.makers.domain.playground.member.profile.UserProfileRanking;

/** Tier 1 캐시: 필터/검색/orderBy 없는 기본 목록의 top50 정렬 결과(members:top50:ids). */
public interface UserProfileRankingCachePort {

  Optional<UserProfileRanking> getTopRanking();

  void putTopRanking(UserProfileRanking ranking);

  void evictTopRanking();
}
