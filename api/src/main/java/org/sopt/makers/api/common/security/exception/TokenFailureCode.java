package org.sopt.makers.api.common.security.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum TokenFailureCode implements FailureCode {
  TOKEN_PARSE_FAILED(400, "토큰 복호화에 실패했습니다."),
  TOKEN_EXPIRED(400, "토큰이 만료되었습니다."),
  UNSUPPORTED_ISSUER(400, "신뢰할 수 없는 발급자입니다."),
  INVALID_PREFIX(400, "토큰 접두사가 잘못되었습니다."),
  INVALID_SIGNATURE(400, "서명이 잘못되었습니다."),
  INVALID_SUBJECT(400, "토큰 Subject가 유효하지 않습니다."),
  INVALID_TOKEN_TYPE(400, "토큰 타입이 유효하지 않습니다."),
  INVALID_REFRESH_TOKEN(401, "유효하지 않은 리프레시 토큰입니다.");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
