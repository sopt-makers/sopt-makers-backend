package org.sopt.makers.api.controller.app.calendar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.CalendarType;

public record CalendarResponse(
    @Schema(description = "일정 기간. 하루짜리면 시작일만, 여러 날이면 물결로 이은 범위", example = "9월 19일 (토)")
        String date,
    @Schema(description = "일정 제목", example = "35기 OT") String title,
    @Schema(description = "일정 종류") CalendarType type,
    @Schema(description = "이 일정이 다가오는 일정인지 여부. 남은 일정이 없으면 모두 false", example = "true")
        @JsonProperty("isRecentSchedule")
        boolean isRecentSchedule) {

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
