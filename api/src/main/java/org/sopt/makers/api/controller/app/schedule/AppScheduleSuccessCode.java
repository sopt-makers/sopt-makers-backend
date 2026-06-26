package org.sopt.makers.api.controller.app.schedule;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AppScheduleSuccessCode implements SuccessCode {
  SUCCESS_GET_SCHEDULES(200, "일정 리스트 조회가 완료되었습니다");

  private final int statusCode;
  private final String message;
}
