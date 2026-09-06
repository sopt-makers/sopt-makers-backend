package org.sopt.makers.api.controller.app.calendar;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.CalendarType;
import org.sopt.makers.domain.app.calendar.exception.CalendarException;
import org.sopt.makers.domain.app.calendar.exception.CalendarFailure;
import org.sopt.makers.domain.app.calendar.service.CalendarService;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

class CalendarControllerTest {

  private static final Calendar SEMINAR =
      new Calendar(
          1L,
          38,
          "세미나",
          true,
          false,
          LocalDate.of(2026, 9, 3),
          LocalDate.of(2026, 9, 3),
          CalendarType.SEMINAR);
  private static final Calendar BREAK =
      new Calendar(
          2L,
          38,
          "방학",
          false,
          false,
          LocalDate.of(2026, 9, 10),
          LocalDate.of(2026, 9, 12),
          CalendarType.BREAK);

  private final CalendarService calendarService = mock(CalendarService.class);
  private final MockMvc mockMvc = AppChannelMockMvc.of(new CalendarController(calendarService), 1L);

  @Test
  void 전체_일정_응답_모양() throws Exception {
    given(calendarService.getAllCurrentGenerationCalendar()).willReturn(List.of(SEMINAR, BREAK));
    given(calendarService.getRecentCalendar(List.of(SEMINAR, BREAK)))
        .willReturn(Optional.of(BREAK));

    mockMvc
        .perform(get("/api/v2/calendar/all"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "전체 일정 조회에 성공했습니다.",
                      "data": [
                        {"date": "9월 3일 (목)", "title": "세미나", "type": "SEMINAR", "isRecentSchedule": false},
                        {"date": "9월 10일 (목) ~ 9월 12일 (토)", "title": "방학", "type": "BREAK", "isRecentSchedule": true}
                      ]
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 다가오는_일정_응답_모양() throws Exception {
    given(calendarService.getRecentCalendarOrLast()).willReturn(SEMINAR);

    mockMvc
        .perform(get("/api/v2/calendar/recent"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "다가오는 일정 조회에 성공했습니다.",
                      "data": {"date": "09-03", "type": "SEMINAR", "title": "세미나"}
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 일정_없음_404_봉투() throws Exception {
    given(calendarService.getRecentCalendarOrLast())
        .willThrow(new CalendarException(CalendarFailure.NOT_FOUND_CALENDAR));

    mockMvc
        .perform(get("/api/v2/calendar/recent"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "일정이 없습니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }
}
