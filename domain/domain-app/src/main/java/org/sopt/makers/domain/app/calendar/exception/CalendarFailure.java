package org.sopt.makers.domain.app.calendar.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CalendarFailure implements FailureCode {
  NOT_FOUND_CALENDAR(404, "일정이 없습니다.");

  private final int statusCode;
  private final String message;
}
