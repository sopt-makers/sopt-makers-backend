package org.sopt.makers.storage.db.playground.member.ask.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.AskReport;
import org.sopt.makers.domain.playground.member.ask.port.AskReportRepositoryPort;
import org.sopt.makers.storage.db.playground.member.ask.entity.AskReportEntity;
import org.sopt.makers.storage.db.playground.member.ask.repository.AskReportJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AskReportRepositoryAdapter implements AskReportRepositoryPort {

  private final AskReportJpaRepository askReportJpaRepository;

  @Transactional
  @Override
  public AskReport save(AskReport askReport) {
    return askReportJpaRepository.save(AskReportEntity.fromDomain(askReport)).toDomain();
  }

  @Override
  public Optional<AskReport> findById(Long reportId) {
    return askReportJpaRepository.findById(reportId).map(AskReportEntity::toDomain);
  }

  @Transactional
  @Override
  public void deleteById(Long reportId) {
    askReportJpaRepository.deleteById(reportId);
  }
}
