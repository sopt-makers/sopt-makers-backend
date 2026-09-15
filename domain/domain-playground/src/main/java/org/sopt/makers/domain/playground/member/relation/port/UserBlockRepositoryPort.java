package org.sopt.makers.domain.playground.member.relation.port;

import java.util.Optional;
import java.util.Set;
import org.sopt.makers.domain.playground.member.relation.UserBlock;

public interface UserBlockRepositoryPort {

  UserBlock save(UserBlock userBlock);

  Optional<UserBlock> findById(Long id);

  Optional<UserBlock> findByBlockerUserIdAndBlockedUserId(Long blockerUserId, Long blockedUserId);

  void deleteById(Long id);

  /** userId가 차단한 사용자 + userId를 차단한 사용자를 모두 포함한 양방향 차단 ID Set을 반환한다. */
  Set<Long> findBlockedUserIdsInvolving(Long userId);
}
