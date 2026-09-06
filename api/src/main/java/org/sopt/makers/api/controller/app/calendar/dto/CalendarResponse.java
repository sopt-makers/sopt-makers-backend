package org.sopt.makers.api.controller.app.calendar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.CalendarType;

public record CalendarResponse(
    String date,
    String title,
    CalendarType type,
    @JsonProperty("isRecentSchedule") boolean isRecentSchedule) {

  private static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN);
  private static final String RANGE_DELIMITER = " ~ ";

  public static CalendarResponse of(Calendar calendar, boolean isRecentSchedule) {
    return new CalendarResponse(
        formatDateRange(calendar), calendar.title(), calendar.type(), isRecentSchedule);
  }

  private static String formatDateRange(Calendar calendar) {
    String start = calendar.startDate().format(DATE_FORMAT);
    if (calendar.isOneDaySchedule()) {
      return start;
    }
    return start + RANGE_DELIMITER + calendar.endDate().format(DATE_FORMAT);
  }
}
