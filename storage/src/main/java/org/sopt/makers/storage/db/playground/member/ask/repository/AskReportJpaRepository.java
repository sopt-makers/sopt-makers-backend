package org.sopt.makers.storage.db.playground.member.ask.repository;

import org.sopt.makers.storage.db.playground.member.ask.entity.AskReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AskReportJpaRepository extends JpaRepository<AskReportEntity, Long> {

  boolean existsByQuestionIdAndReporterUserId(Long questionId, Long reporterUserId);
}
