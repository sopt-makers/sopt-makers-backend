package org.sopt.makers.api.controller.admin.attendance.dto;

import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;

public record SubAttendanceUpdateResponse(Long subAttendanceId, AttendanceStatus status) {
  public static SubAttendanceUpdateResponse from(SubAttendance attendance) {
    return new SubAttendanceUpdateResponse(attendance.id(), attendance.status());
  }
}
