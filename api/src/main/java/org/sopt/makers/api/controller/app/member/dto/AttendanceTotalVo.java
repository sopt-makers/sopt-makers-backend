package org.sopt.makers.api.controller.app.member.dto;

import java.time.format.DateTimeFormatter;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.LectureAttribute;

public record AttendanceTotalVo(
    LectureAttribute attribute, String name, AttendanceStatus status, String date) {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M월 d일");

  public static AttendanceTotalVo from(Attendance attendance) {
    return new AttendanceTotalVo(
        attendance.attribute(),
        attendance.lectureName(),
        attendance.status(),
        attendance.lectureStartAt().format(DATE_FORMATTER));
  }
}
