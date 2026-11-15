package org.sopt.makers.api.controller.app.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.app.AppMemberAttendanceSummary;

public record AttendanceTotalResponse(
    @Schema(description = "현재 기수 파트") Part part,
    @Schema(description = "현재 활동 기수", example = "35") int generation,
    @Schema(description = "유저 이름", example = "김앱짱") String name,
    @Schema(description = "출석 점수. 값이 없으면 기본 점수 2점", example = "2.0") float score,
    @Schema(description = "상태별 출석 횟수 집계") AttendanceStatusListResponse total,
    @Schema(description = "종료된 세션의 출석 내역. 세션 시작 일시 내림차순") List<AttendanceTotalVo> attendances) {

  public static AttendanceTotalResponse from(AppMemberAttendanceSummary summary) {
    return new AttendanceTotalResponse(
        summary.member().part(),
        summary.member().generation(),
        summary.member().name(),
        summary.member().attendanceScore(),
        AttendanceStatusListResponse.from(summary),
        summary.visibleAttendances().stream().map(AttendanceTotalVo::from).toList());
  }
}
