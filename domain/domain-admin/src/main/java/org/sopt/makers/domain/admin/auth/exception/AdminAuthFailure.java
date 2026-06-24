package org.sopt.makers.domain.admin.auth.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdminAuthFailure implements FailureCode {
  DUPLICATED_EMAIL(400, "이미 가입된 이메일입니다"),
  INVALID_EMAIL(401, "존재하지 않는 이메일입니다"),
  INVALID_PASSWORD(401, "비밀번호가 올바르지 않습니다"),
  NOT_APPROVED_ACCOUNT(403, "아직 승인되지 않은 계정입니다"),
  NOT_FOUND_ADMIN(404, "존재하지 않는 어드민입니다");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
