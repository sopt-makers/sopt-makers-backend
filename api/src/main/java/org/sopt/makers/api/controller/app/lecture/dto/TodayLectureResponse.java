package org.sopt.makers.api.controller.app.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.sopt.makers.domain.admin.app.AppLecture;
import org.sopt.makers.domain.admin.app.AppLectureResponseType;
import org.sopt.makers.domain.admin.app.AppLectureResult;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;

public record TodayLectureResponse(
    @Schema(description = "세션 응답 종류. 출석이 없는 세션인지, 출석 대상인지 구분한다") AppLectureResponseType type,
    @Schema(description = "세션 아이디. 오늘 세션이 없으면 0", example = "1") long id,
    @Schema(description = "세션 장소. 오늘 세션이 없으면 빈 문자열", example = "건국대학교 새천년관") String location,
    @Schema(description = "세션 이름. 오늘 세션이 없으면 빈 문자열", example = "1차 세미나") String name,
    @Schema(description = "세션 시작 일시. 오늘 세션이 없으면 빈 문자열", example = "2026-09-19T14:00:00")
        String startDate,
    @Schema(description = "세션 종료 일시. 오늘 세션이 없으면 빈 문자열", example = "2026-09-19T18:00:00")
        String endDate,
    @Schema(description = "클라이언트에 띄울 안내 문구", example = "세미나 출석을 진행해주세요") String message,
    @Schema(description = "차수별 출석 결과. 출석 전이거나 출석이 없는 세션이면 빈 배열")
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

  public record LectureGetResponse(
      @Schema(description = "해당 차수 출석 상태") AttendanceStatus status,
      @Schema(description = "출석 처리된 일시. 아직 출석 전이면 빈 문자열", example = "2026-09-19T14:05:00")
          String attendedAt) {

    private static LectureGetResponse from(SubAttendance subAttendance) {
      return new LectureGetResponse(subAttendance.status(), format(subAttendance.attendedAt()));
    }
  }
}
