package org.sopt.makers.domain.admin.attendance;

import java.time.LocalDateTime;

public record SubAttendance(
    Long id,
    Long attendanceId,
    Long subLectureId,
    int round,
    LocalDateTime subLectureStartAt,
    AttendanceStatus status,
    LocalDateTime attendedAt) {
  public SubAttendance withStatus(AttendanceStatus newStatus) {
    return new SubAttendance(
        id, attendanceId, subLectureId, round, subLectureStartAt, newStatus, attendedAt);
  }

  public SubAttendance markAttendance(LocalDateTime attendedAt) {
    return new SubAttendance(
        id,
        attendanceId,
        subLectureId,
        round,
        subLectureStartAt,
        AttendanceStatus.ATTENDANCE,
        attendedAt);
  }
}
