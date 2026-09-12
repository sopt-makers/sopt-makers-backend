package org.sopt.makers.api.controller.app.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.format.DateTimeFormatter;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;

public record AttendanceTotalVo(
    @Schema(description = "세션 종류") LectureAttribute attribute,
    @Schema(description = "세션 이름", example = "1차 세미나") String name,
    @Schema(description = "출석 상태") AttendanceStatus status,
    @Schema(description = "세션 진행일", example = "9월 19일") String date) {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M월 d일");

  public static AttendanceTotalVo from(Attendance attendance) {
    return new AttendanceTotalVo(
        attendance.attribute(),
        attendance.lectureName(),
        attendance.status(),
        attendance.lectureStartAt().format(DATE_FORMATTER));
  }
}
