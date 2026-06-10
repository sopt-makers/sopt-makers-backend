package org.sopt.makers.clients.oauth.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum OAuthFailure implements FailureCode {
  INVALID_ID_TOKEN(400, "ID 토큰 유효성 검사에 실패했습니다."),
  PUBLIC_KEY_SET_FETCH_FAILED(503, "OAuth 공개키 조회에 실패했습니다.");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
