package org.sopt.makers.api.controller.app.calendar;

import static org.sopt.makers.api.controller.app.calendar.CalendarSuccessCode.GET_ALL_CALENDAR;
import static org.sopt.makers.api.controller.app.calendar.CalendarSuccessCode.GET_RECENT_CALENDAR;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.app.calendar.dto.CalendarResponse;
import org.sopt.makers.api.controller.app.calendar.dto.RecentCalendarResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.service.CalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/calendar")
@RequiredArgsConstructor
public class CalendarController implements CalendarApi {

  private final CalendarService calendarService;

  @Override
  @GetMapping("/all")
  public ResponseEntity<BaseResponse<List<CalendarResponse>>> getAllCalendar() {
    List<Calendar> calendars = calendarService.getAllCurrentGenerationCalendar();
    Long recentId = calendarService.getRecentCalendar(calendars).map(Calendar::id).orElse(null);
    return ResponseFactory.typedSuccess(
        GET_ALL_CALENDAR,
        calendars.stream()
            .map(calendar -> CalendarResponse.of(calendar, calendar.id().equals(recentId)))
            .toList());
  }

  @Override
  @GetMapping("/recent")
  public ResponseEntity<BaseResponse<RecentCalendarResponse>> getRecentCalendar() {
    return ResponseFactory.typedSuccess(
        GET_RECENT_CALENDAR, RecentCalendarResponse.of(calendarService.getRecentCalendarOrLast()));
  }
}
