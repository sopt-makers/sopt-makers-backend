package org.sopt.makers.storage.db.playground.report.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.report.SoptReportStats;
import org.sopt.makers.domain.playground.report.port.SoptReportStatsRepositoryPort;
import org.sopt.makers.storage.db.playground.report.entity.SoptReportStatsEntity;
import org.sopt.makers.storage.db.playground.report.repository.SoptReportStatsJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptReportStatsRepositoryAdapter implements SoptReportStatsRepositoryPort {

  private final SoptReportStatsJpaRepository soptReportStatsJpaRepository;

  @Override
  public List<SoptReportStats> findByCategory(String category) {
    return soptReportStatsJpaRepository.findByCategory(category).stream()
        .map(SoptReportStatsEntity::toDomain)
        .toList();
  }
}
