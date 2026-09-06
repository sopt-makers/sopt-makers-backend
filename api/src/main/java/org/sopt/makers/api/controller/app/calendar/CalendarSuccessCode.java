package org.sopt.makers.api.controller.app.calendar;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CalendarSuccessCode implements SuccessCode {
  GET_ALL_CALENDAR(200, "전체 일정 조회에 성공했습니다."),
  GET_RECENT_CALENDAR(200, "다가오는 일정 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
