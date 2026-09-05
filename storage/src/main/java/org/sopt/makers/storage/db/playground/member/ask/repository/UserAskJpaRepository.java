package org.sopt.makers.storage.db.playground.member.ask.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.member.ask.entity.UserAskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAskJpaRepository extends JpaRepository<UserAskEntity, Long> {

  @Query(
      "SELECT DISTINCT u.anonymousNicknameId FROM UserAskEntity u "
          + "WHERE u.receiverUserId = :receiverUserId AND u.anonymousNicknameId IS NOT NULL")
  List<Long> findDistinctAnonymousNicknameIdsByReceiverUserId(
      @Param("receiverUserId") Long receiverUserId);
}
