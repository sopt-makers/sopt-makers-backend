package org.sopt.makers.api.controller.official.notification;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum NotificationSuccessCode implements SuccessCode {
  REGISTER_NOTIFICATION(201, "모집 알림 신청에 성공했습니다."),
  GET_NOTIFICATION_LIST(200, "모집 알림 목록 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
