package org.sopt.makers.domain.admin.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubAttendance {

  private final Long id;
  private final Long attendanceId;
  private final int round;
  private final AttendanceStatus status;

  public SubAttendance withStatus(AttendanceStatus newStatus) {
    return new SubAttendance(id, attendanceId, round, newStatus);
  }
}
