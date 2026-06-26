package org.sopt.makers.api.controller.app.schedule.dto;

import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.KOREAN;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.sopt.makers.domain.admin.app.AppSchedule;
import org.sopt.makers.domain.admin.attendance.LectureAttribute;

public record ScheduleListResponse(List<DateResponse> dates) {

  public static ScheduleListResponse from(Map<LocalDate, List<AppSchedule>> scheduleMap) {
    return new ScheduleListResponse(
        scheduleMap.keySet().stream()
            .sorted()
            .map(date -> DateResponse.from(date, scheduleMap.get(date)))
            .toList());
  }

  public record DateResponse(String date, String dayOfWeek, List<ScheduleResponse> schedules) {

    private static DateResponse from(LocalDate date, List<AppSchedule> schedules) {
      return new DateResponse(
          date.toString(),
          date.getDayOfWeek().getDisplayName(SHORT, KOREAN),
          schedules.stream().map(ScheduleResponse::from).toList());
    }
  }

  public record ScheduleResponse(
      long scheduleId, String startDate, String endDate, LectureAttribute attribute, String title) {

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
