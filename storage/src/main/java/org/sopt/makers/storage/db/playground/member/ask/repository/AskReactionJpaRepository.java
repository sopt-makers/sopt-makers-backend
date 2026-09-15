package org.sopt.makers.storage.db.playground.member.ask.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.member.ask.entity.AskReactionEntity;
import org.sopt.makers.storage.db.playground.member.ask.projection.AskReactionCountRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AskReactionJpaRepository extends JpaRepository<AskReactionEntity, Long> {

  Optional<AskReactionEntity> findByQuestionIdAndReactorUserId(Long askId, Long reactorUserId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM AskReactionEntity r WHERE r.questionId = :askId")
  void deleteAllByAskId(@Param("askId") Long askId);

  @Query(
      "SELECT new org.sopt.makers.storage.db.playground.member.ask.projection.AskReactionCountRow(r.questionId, COUNT(r)) "
          + "FROM AskReactionEntity r WHERE r.questionId IN :askIds GROUP BY r.questionId")
  List<AskReactionCountRow> countGroupedByAskIds(@Param("askIds") List<Long> askIds);

  @Query(
      "SELECT r.questionId FROM AskReactionEntity r "
          + "WHERE r.questionId IN :askIds AND r.reactorUserId = :reactorUserId")
  List<Long> findReactedAskIds(
      @Param("askIds") List<Long> askIds, @Param("reactorUserId") Long reactorUserId);
}
