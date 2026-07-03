package org.sopt.makers.api.controller.admin.alarm;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdminAlarmSuccessCode implements SuccessCode {
  SUCCESS_SEND_ALARM(200, "알림 즉시 발송이 완료되었습니다"),
  SUCCESS_SCHEDULE_ALARM(200, "알림 예약 발송이 완료되었습니다"),
  SUCCESS_GET_ALARMS(200, "알림 목록 조회가 완료되었습니다"),
  SUCCESS_GET_ALARM(200, "알림 상세 조회가 완료되었습니다"),
  SUCCESS_DELETE_ALARM(200, "알림 삭제가 완료되었습니다"),
  SUCCESS_UPDATE_ALARM_STATUS(200, "알림 상태 업데이트가 완료되었습니다");

  private final int statusCode;
  private final String message;
}
