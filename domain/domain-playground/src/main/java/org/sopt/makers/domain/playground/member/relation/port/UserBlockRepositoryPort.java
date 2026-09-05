package org.sopt.makers.domain.playground.member.relation.port;

import java.util.Optional;
import org.sopt.makers.domain.playground.member.relation.UserBlock;

public interface UserBlockRepositoryPort {

  UserBlock save(UserBlock userBlock);

  Optional<UserBlock> findById(Long id);

  Optional<UserBlock> findByBlockerUserIdAndBlockedUserId(Long blockerUserId, Long blockedUserId);

  void deleteById(Long id);
}
