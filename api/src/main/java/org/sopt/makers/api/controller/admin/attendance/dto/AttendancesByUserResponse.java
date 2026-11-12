package org.sopt.makers.api.controller.admin.attendance.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceDetail;
import org.sopt.makers.domain.admin.attendance.UserAttendanceSummary;

public record AttendancesByUserResponse(
    String name, float score, String part, List<LectureResponse> lectures) {

  public static AttendancesByUserResponse from(UserAttendanceSummary summary) {
    var activity = summary.activity();
    return new AttendancesByUserResponse(
        activity.name(),
        activity.attendanceScore() != null ? activity.attendanceScore() : Attendance.BASE_SCORE,
        activity.part().getName(),
        summary.lectures().stream().map(LectureResponse::from).toList());
  }

  public record LectureResponse(
      LocalDateTime startDate,
      LocalDateTime endDate,
      String lecture,
      float additiveScore,
      String status,
      List<AttendanceResponse> attendances) {

    public static LectureResponse from(AttendanceDetail detail) {
      Attendance attendance = detail.attendance();
      return new LectureResponse(
          detail.startDate(),
          detail.endDate(),
          attendance.lectureName(),
          attendance.computeScore(),
          attendance.status().getName(),
          detail.subAttendances().stream().map(AttendanceResponse::from).toList());
    }
  }

  public record AttendanceResponse(int round, String status, String date) {

    public static AttendanceResponse from(AttendanceDetail.SubAttendanceDetail detail) {
      return new AttendanceResponse(
          detail.round(), detail.status().getName(), detail.updatedAt().toString());
    }
  }
}
