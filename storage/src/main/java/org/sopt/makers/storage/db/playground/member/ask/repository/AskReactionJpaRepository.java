package org.sopt.makers.storage.db.playground.member.ask.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.member.ask.entity.AskReactionEntity;
import org.sopt.makers.storage.db.playground.member.ask.projection.AskReactionCountRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AskReactionJpaRepository extends JpaRepository<AskReactionEntity, Long> {

  Optional<AskReactionEntity> findByQuestionIdAndReactorUserId(Long questionId, Long reactorUserId);

  @Query(
      "SELECT new org.sopt.makers.storage.db.playground.member.ask.projection.AskReactionCountRow(r.questionId, COUNT(r)) "
          + "FROM AskReactionEntity r WHERE r.questionId IN :questionIds GROUP BY r.questionId")
  List<AskReactionCountRow> countGroupedByQuestionIds(@Param("questionIds") List<Long> questionIds);

  @Query(
      "SELECT r.questionId FROM AskReactionEntity r "
          + "WHERE r.questionId IN :questionIds AND r.reactorUserId = :reactorUserId")
  List<Long> findReactedQuestionIds(
      @Param("questionIds") List<Long> questionIds, @Param("reactorUserId") Long reactorUserId);
}
