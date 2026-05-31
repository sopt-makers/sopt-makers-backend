package org.sopt.makers.domain.admin.attendance;

public record SubAttendance(Long id, Long attendanceId, int round, AttendanceStatus status) {
  public SubAttendance withStatus(AttendanceStatus newStatus) {
    return new SubAttendance(id, attendanceId, round, newStatus);
  }
}
