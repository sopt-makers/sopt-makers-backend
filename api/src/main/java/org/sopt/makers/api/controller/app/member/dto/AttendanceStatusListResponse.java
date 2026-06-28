package org.sopt.makers.api.controller.app.member.dto;

import org.sopt.makers.domain.admin.app.AppMemberAttendanceSummary;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;

public record AttendanceStatusListResponse(int attendance, int absent, int tardy, int participate) {

  public static AttendanceStatusListResponse from(AppMemberAttendanceSummary summary) {
    return new AttendanceStatusListResponse(
        summary.countByStatus(AttendanceStatus.ATTENDANCE),
        summary.countByStatus(AttendanceStatus.ABSENT),
        summary.countByStatus(AttendanceStatus.TARDY),
        summary.countByStatus(AttendanceStatus.PARTICIPATE));
  }
}
