package org.sopt.makers.api.controller.app.member.dto;

import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.app.AppMemberAttendanceSummary;

public record AttendanceTotalResponse(
    Part part,
    int generation,
    String name,
    float score,
    AttendanceStatusListResponse total,
    List<AttendanceTotalVo> attendances) {

  public static AttendanceTotalResponse from(AppMemberAttendanceSummary summary) {
    return new AttendanceTotalResponse(
        summary.activity().part(),
        summary.activity().generation(),
        summary.activity().name(),
        summary.activity().attendanceScore() == null ? 0f : summary.activity().attendanceScore(),
        AttendanceStatusListResponse.from(summary),
        summary.visibleAttendances().stream().map(AttendanceTotalVo::from).toList());
  }
}
