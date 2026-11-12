package org.sopt.makers.domain.admin.attendance;

import java.time.LocalDateTime;
import java.util.List;

public record AttendanceDetail(
    Attendance attendance,
    LocalDateTime startDate,
    LocalDateTime endDate,
    List<SubAttendanceDetail> subAttendances) {

  public record SubAttendanceDetail(int round, AttendanceStatus status, LocalDateTime updatedAt) {}
}
