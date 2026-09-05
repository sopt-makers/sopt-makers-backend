package org.sopt.makers.storage.db.playground.member.relation.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.playground.member.relation.entity.UserBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBlockJpaRepository extends JpaRepository<UserBlockEntity, Long> {

  Optional<UserBlockEntity> findByBlockerUserIdAndBlockedUserId(
      Long blockerUserId, Long blockedUserId);
}
