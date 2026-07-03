package org.sopt.makers.domain.admin.alarm.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AlarmFailure implements FailureCode {
  NOT_FOUND_ALARM(404, "알림이 존재하지 않습니다"),
  INVALID_ALARM_TARGET_TYPE(400, "올바르지 않은 알림 타겟 타입입니다"),
  INVALID_SCHEDULE_ALARM_FORMAT(400, "알림 예약 시간 포맷이 맞지 않습니다"),
  FAIL_SEND_ALARM(400, "알림 즉시 발송에 실패하였습니다"),
  FAIL_SCHEDULE_ALARM(400, "알림 예약 발송에 실패하였습니다"),
  FAIL_DELETE_SCHEDULE_ALARM(400, "예약 알림 삭제에 실패하였습니다");

  private final int statusCode;
  private final String message;
}
