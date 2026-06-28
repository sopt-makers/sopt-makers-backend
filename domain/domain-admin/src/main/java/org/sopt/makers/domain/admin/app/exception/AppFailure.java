package org.sopt.makers.domain.admin.app.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AppFailure implements FailureCode {
  INVALID_SCHEDULE_DATE_RANGE(400, "조회할 수 없는 일정 기간입니다");

  private final int statusCode;
  private final String message;
}
