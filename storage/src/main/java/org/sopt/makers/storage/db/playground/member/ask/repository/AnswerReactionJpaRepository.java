package org.sopt.makers.storage.db.playground.member.ask.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.member.ask.entity.AnswerReactionEntity;
import org.sopt.makers.storage.db.playground.member.ask.projection.AnswerReactionCountRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerReactionJpaRepository extends JpaRepository<AnswerReactionEntity, Long> {

  Optional<AnswerReactionEntity> findByAnswerIdAndReactorUserId(Long answerId, Long reactorUserId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM AnswerReactionEntity r WHERE r.answerId = :answerId")
  void deleteAllByAnswerId(@Param("answerId") Long answerId);

  @Query(
      "SELECT new org.sopt.makers.storage.db.playground.member.ask.projection.AnswerReactionCountRow(r.answerId, COUNT(r)) "
          + "FROM AnswerReactionEntity r WHERE r.answerId IN :answerIds GROUP BY r.answerId")
  List<AnswerReactionCountRow> countGroupedByAnswerIds(@Param("answerIds") List<Long> answerIds);

  @Query(
      "SELECT r.answerId FROM AnswerReactionEntity r "
          + "WHERE r.answerId IN :answerIds AND r.reactorUserId = :reactorUserId")
  List<Long> findReactedAnswerIds(
      @Param("answerIds") List<Long> answerIds, @Param("reactorUserId") Long reactorUserId);
}
