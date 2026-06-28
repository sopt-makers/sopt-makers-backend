package org.sopt.makers.api.controller.app.lecture.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.sopt.makers.domain.admin.app.AppLecture;
import org.sopt.makers.domain.admin.app.AppLectureResponseType;
import org.sopt.makers.domain.admin.app.AppLectureResult;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;

public record TodayLectureResponse(
    AppLectureResponseType type,
    long id,
    String location,
    String name,
    String startDate,
    String endDate,
    String message,
    List<LectureGetResponse> attendances) {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

  public static TodayLectureResponse from(AppLectureResult result) {
    AppLecture lecture = result.lecture();
    if (lecture == null) {
      return new TodayLectureResponse(
          result.type(), 0L, "", "", "", "", result.message(), List.of());
    }

    return new TodayLectureResponse(
        result.type(),
        lecture.id(),
        lecture.location(),
        lecture.name(),
        format(lecture.startAt()),
        format(lecture.endAt()),
        result.message(),
        result.attendances().stream().map(LectureGetResponse::from).toList());
  }

  private static String format(LocalDateTime dateTime) {
    return dateTime == null ? "" : dateTime.format(DATE_TIME_FORMATTER);
  }

  public record LectureGetResponse(AttendanceStatus status, String attendedAt) {

    private static LectureGetResponse from(SubAttendance subAttendance) {
      return new LectureGetResponse(subAttendance.status(), format(subAttendance.attendedAt()));
    }
  }
}
