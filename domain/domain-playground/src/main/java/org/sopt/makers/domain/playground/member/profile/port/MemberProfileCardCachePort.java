package org.sopt.makers.domain.playground.member.profile.port;

import java.util.Collection;
import java.util.Map;
import org.sopt.makers.domain.user.User;

/** Tier 2 캐시: 회원별 정적 프로필 카드(member:card:{id}). isCoffeeChatActivate/questionPreview는 포함하지 않는다. */
public interface MemberProfileCardCachePort {

  Map<Long, User> getAllPresent(Collection<Long> userIds);

  void put(User user);

  void evict(Long userId);
}
