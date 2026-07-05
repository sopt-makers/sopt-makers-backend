package org.sopt.makers.domain.admin.attendance.port;

import java.util.Map;

public interface AttendanceUserActivityPort {

  void updateAttendanceScore(Long userId, int generation, Float score);

  void bulkUpdateAttendanceScores(int generation, Map<Long, Float> userScores);
}
