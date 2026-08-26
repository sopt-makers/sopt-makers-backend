package org.sopt.makers.api.controller.app.notification;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AppNotificationSuccessCode implements SuccessCode {
  GET_NOTIFICATIONS(200, "알림 목록 조회에 성공했습니다."),
  GET_NOTIFICATION_DETAIL(200, "알림 상세 조회에 성공했습니다."),
  UPDATE_NOTIFICATION_READ(200, "알림 읽음 처리에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
