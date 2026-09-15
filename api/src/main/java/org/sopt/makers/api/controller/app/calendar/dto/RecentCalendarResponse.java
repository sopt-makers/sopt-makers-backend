package org.sopt.makers.api.controller.app.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.format.DateTimeFormatter;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.CalendarType;

public record RecentCalendarResponse(
    @Schema(description = "일정 시작일", example = "09-19") String date,
    @Schema(description = "일정 종류") CalendarType type,
    @Schema(description = "일정 제목", example = "35기 OT") String title) {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd");

  public static RecentCalendarResponse of(Calendar calendar) {
    return new RecentCalendarResponse(
        calendar.startDate().format(DATE_FORMAT), calendar.type(), calendar.title());
  }
}
