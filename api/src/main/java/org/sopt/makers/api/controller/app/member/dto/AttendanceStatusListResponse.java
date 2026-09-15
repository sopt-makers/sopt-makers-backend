package org.sopt.makers.api.controller.app.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.admin.app.AppMemberAttendanceSummary;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;

public record AttendanceStatusListResponse(
    @Schema(description = "출석 횟수", example = "5") int attendance,
    @Schema(description = "결석 횟수", example = "1") int absent,
    @Schema(description = "지각 횟수", example = "1") int tardy,
    @Schema(description = "참여 횟수. 세션 종류가 기타일 때 쓴다", example = "2") int participate) {

  public static AttendanceStatusListResponse from(AppMemberAttendanceSummary summary) {
    return new AttendanceStatusListResponse(
        summary.countByStatus(AttendanceStatus.ATTENDANCE),
        summary.countByStatus(AttendanceStatus.ABSENT),
        summary.countByStatus(AttendanceStatus.TARDY),
        summary.countByStatus(AttendanceStatus.PARTICIPATE));
  }
}
