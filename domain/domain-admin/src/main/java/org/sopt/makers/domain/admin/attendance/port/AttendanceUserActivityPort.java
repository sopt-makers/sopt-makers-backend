package org.sopt.makers.domain.admin.attendance.port;

public interface AttendanceUserActivityPort {

  void updateAttendanceScore(Long userId, int generation, Float score);
}
