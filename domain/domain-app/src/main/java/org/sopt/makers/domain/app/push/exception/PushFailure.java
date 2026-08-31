package org.sopt.makers.domain.app.push.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum PushFailure implements FailureCode {
  INVALID_PUSH_TOKEN_PLATFORM(400, "지원하지 않는 푸시 토큰 플랫폼입니다."),
  FAIL_SEND_PUSH(500, "푸시 발송에 실패했습니다."),
  FAIL_MANAGE_PUSH_TOKEN(500, "푸시 토큰 등록/해지에 실패했습니다.");

  private final int statusCode;
  private final String message;
}
