package org.sopt.makers.storage.db.playground.report.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.report.entity.SoptReportStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptReportStatsJpaRepository extends JpaRepository<SoptReportStatsEntity, Long> {

  List<SoptReportStatsEntity> findByCategory(String category);
}
