package org.sopt.makers.domain.playground.report.port;

import java.util.List;
import org.sopt.makers.domain.playground.report.SoptReportStats;

public interface SoptReportStatsRepositoryPort {

  List<SoptReportStats> findByCategory(String category);
}
