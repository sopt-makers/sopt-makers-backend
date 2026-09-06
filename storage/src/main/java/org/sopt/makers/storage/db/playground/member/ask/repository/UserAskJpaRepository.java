package org.sopt.makers.storage.db.playground.member.ask.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.storage.db.playground.member.ask.entity.UserAskEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAskJpaRepository extends JpaRepository<UserAskEntity, Long> {

  @Query(
      "SELECT DISTINCT u.anonymousNicknameId FROM UserAskEntity u "
          + "WHERE u.receiverUserId = :receiverUserId AND u.anonymousNicknameId IS NOT NULL")
  List<Long> findDistinctAnonymousNicknameIdsByReceiverUserId(
      @Param("receiverUserId") Long receiverUserId);

  @Query("SELECT q FROM UserAskEntity q WHERE q.receiverUserId = :receiverUserId ORDER BY q.createdAt DESC, q.id DESC")
  List<UserAskEntity> findAllByReceiverUserId(
      @Param("receiverUserId") Long receiverUserId, Pageable pageable);

  @Query(
      "SELECT q FROM UserAskEntity q WHERE q.receiverUserId = :receiverUserId "
          + "AND EXISTS (SELECT 1 FROM UserAnswerEntity a WHERE a.questionId = q.id) "
          + "ORDER BY q.createdAt DESC, q.id DESC")
  List<UserAskEntity> findAnsweredByReceiverUserId(
      @Param("receiverUserId") Long receiverUserId, Pageable pageable);

  @Query(
      "SELECT q FROM UserAskEntity q WHERE q.receiverUserId = :receiverUserId "
          + "AND NOT EXISTS (SELECT 1 FROM UserAnswerEntity a WHERE a.questionId = q.id) "
          + "ORDER BY q.createdAt DESC, q.id DESC")
  List<UserAskEntity> findUnansweredByReceiverUserId(
      @Param("receiverUserId") Long receiverUserId, Pageable pageable);

  long countByReceiverUserId(@Param("receiverUserId") Long receiverUserId);

  boolean existsByReceiverUserIdAndCreatedAtAfter(Long receiverUserId, LocalDateTime since);

  @Query(
      "SELECT COUNT(q) FROM UserAskEntity q WHERE q.receiverUserId = :receiverUserId "
          + "AND EXISTS (SELECT 1 FROM UserAnswerEntity a WHERE a.questionId = q.id)")
  long countAnsweredByReceiverUserId(@Param("receiverUserId") Long receiverUserId);

  @Query(
      "SELECT COUNT(q) FROM UserAskEntity q WHERE q.receiverUserId = :receiverUserId "
          + "AND NOT EXISTS (SELECT 1 FROM UserAnswerEntity a WHERE a.questionId = q.id)")
  long countUnansweredByReceiverUserId(@Param("receiverUserId") Long receiverUserId);

  @Query(
      "SELECT q FROM UserAskEntity q WHERE q.askerUserId = :askerUserId AND q.receiverUserId = :receiverUserId "
          + "AND EXISTS (SELECT 1 FROM UserAnswerEntity a WHERE a.questionId = q.id) "
          + "ORDER BY q.createdAt DESC, q.id DESC")
  List<UserAskEntity> findAllAnsweredByAskerUserIdAndReceiverUserIdOrderByLatest(
      @Param("askerUserId") Long askerUserId, @Param("receiverUserId") Long receiverUserId);

  @Query(
      "SELECT q.id FROM UserAskEntity q WHERE q.receiverUserId = :receiverUserId "
          + "AND EXISTS (SELECT 1 FROM UserAnswerEntity a WHERE a.questionId = q.id) "
          + "ORDER BY q.createdAt DESC, q.id DESC")
  List<Long> findAllAnsweredIdsByReceiverUserIdOrderByLatest(@Param("receiverUserId") Long receiverUserId);

  @Query(
      "SELECT COUNT(q) FROM UserAskEntity q, UserAnswerEntity a "
          + "WHERE q.receiverUserId = :receiverUserId AND a.questionId = q.id "
          + "AND (a.createdAt > :answerCreatedAt OR (a.createdAt = :answerCreatedAt AND q.id > :questionId))")
  long countAnsweredBeforeTargetInLatestOrder(
      @Param("receiverUserId") Long receiverUserId,
      @Param("answerCreatedAt") LocalDateTime answerCreatedAt,
      @Param("questionId") Long questionId);

  @Query(
      "SELECT COUNT(q) FROM UserAskEntity q WHERE q.receiverUserId = :receiverUserId "
          + "AND NOT EXISTS (SELECT 1 FROM UserAnswerEntity a WHERE a.questionId = q.id) "
          + "AND (q.createdAt > :askCreatedAt OR (q.createdAt = :askCreatedAt AND q.id > :questionId))")
  long countUnansweredBeforeTargetInLatestOrder(
      @Param("receiverUserId") Long receiverUserId,
      @Param("askCreatedAt") LocalDateTime askCreatedAt,
      @Param("questionId") Long questionId);

  @Query(
      "SELECT q FROM UserAskEntity q, UserAnswerEntity a "
          + "WHERE a.questionId = q.id AND q.isReported = false "
          + "ORDER BY a.createdAt DESC, q.id DESC")
  List<UserAskEntity> findLatestAnswered(Pageable pageable);
}
