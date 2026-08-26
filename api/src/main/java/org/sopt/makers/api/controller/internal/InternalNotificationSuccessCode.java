package org.sopt.makers.api.controller.internal;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum InternalNotificationSuccessCode implements SuccessCode {
  REGISTER_NOTIFICATION(200, "인앱 알림 등록에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
