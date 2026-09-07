package org.sopt.makers.storage.db.playground.member.ask.repository;

import org.sopt.makers.storage.db.playground.member.ask.entity.AskReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AskReportJpaRepository extends JpaRepository<AskReportEntity, Long> {

  boolean existsByQuestionIdAndReporterUserId(Long questionId, Long reporterUserId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM AskReportEntity r WHERE r.questionId = :questionId")
  void deleteAllByQuestionId(@Param("questionId") Long questionId);
}
