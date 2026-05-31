package org.sopt.makers.api.controller.app.attendance;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AppAttendanceSuccessCode implements SuccessCode {
  SUCCESS_ATTEND(200, "출석 체크가 완료되었습니다");

  private final int statusCode;
  private final String message;
}
