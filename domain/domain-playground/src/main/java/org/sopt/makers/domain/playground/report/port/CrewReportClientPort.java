package org.sopt.makers.domain.playground.report.port;

import java.util.List;

public interface CrewReportClientPort {

  List<String> getFastestAppliedGroupTitles(Long userId, int limit, int year);
}
