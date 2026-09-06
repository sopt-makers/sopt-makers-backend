package org.sopt.makers.api.controller.app.calendar.dto;

import java.time.format.DateTimeFormatter;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.CalendarType;

public record RecentCalendarResponse(String date, CalendarType type, String title) {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd");

  public static RecentCalendarResponse of(Calendar calendar) {
    return new RecentCalendarResponse(
        calendar.startDate().format(DATE_FORMAT), calendar.type(), calendar.title());
  }
}
