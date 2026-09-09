package org.sopt.makers.storage.db.playground.member.relation.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.member.relation.entity.UserBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBlockJpaRepository extends JpaRepository<UserBlockEntity, Long> {

  Optional<UserBlockEntity> findByBlockerUserIdAndBlockedUserId(
      Long blockerUserId, Long blockedUserId);

  @Query(
      "SELECT CASE WHEN e.blockerUserId = :userId THEN e.blockedUserId ELSE e.blockerUserId END "
          + "FROM UserBlockEntity e "
          + "WHERE (e.blockerUserId = :userId OR e.blockedUserId = :userId) AND e.isBlocked = true")
  List<Long> findBlockedCounterpartUserIds(@Param("userId") Long userId);
}
