package org.sopt.makers.api.controller.app.schedule.dto;

import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.KOREAN;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.sopt.makers.domain.admin.app.AppSchedule;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;

public record ScheduleListResponse(
    @Schema(description = "조회 기간의 모든 날짜. 일정이 없는 날짜도 빈 목록으로 담긴다. 날짜 오름차순")
        List<DateResponse> dates) {

  public static ScheduleListResponse from(Map<LocalDate, List<AppSchedule>> scheduleMap) {
    return new ScheduleListResponse(
        scheduleMap.keySet().stream()
            .sorted()
            .map(date -> DateResponse.from(date, scheduleMap.getOrDefault(date, List.of())))
            .toList());
  }

  public record DateResponse(
      @Schema(description = "일정 날짜", example = "2026-09-19") String date,
      @Schema(description = "요일 한 글자", example = "토") String dayOfWeek,
      @Schema(description = "그 날의 일정 목록. 없으면 빈 배열") List<ScheduleResponse> schedules) {

    private static DateResponse from(LocalDate date, List<AppSchedule> schedules) {
      return new DateResponse(
          date.toString(),
          date.getDayOfWeek().getDisplayName(SHORT, KOREAN),
          schedules.stream().map(ScheduleResponse::from).toList());
    }
  }

  public record ScheduleResponse(
      @Schema(description = "일정 아이디", example = "1") long scheduleId,
      @Schema(description = "시작 일시", example = "2026-09-19T14:00:00") String startDate,
      @Schema(description = "종료 일시", example = "2026-09-19T18:00:00") String endDate,
      @Schema(description = "일정 종류") LectureAttribute attribute,
      @Schema(description = "일정 제목", example = "OT") String title) {

    private static ScheduleResponse from(AppSchedule schedule) {
      return new ScheduleResponse(
          schedule.id(),
          schedule.startAt().toString(),
          schedule.endAt().toString(),
          schedule.attribute(),
          schedule.title());
    }
  }
}
