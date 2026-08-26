package org.sopt.makers.api.controller.app.push;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum PushTokenSuccessCode implements SuccessCode {
  REGISTER_PUSH_TOKEN(200, "푸시 토큰 등록에 성공했습니다."),
  DELETE_PUSH_TOKEN(200, "푸시 토큰 해제에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
