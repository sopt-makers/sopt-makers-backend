package org.sopt.makers.domain.auth.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AuthFailure implements FailureCode {

  // 400
  INVALID_SOCIAL_PLATFORM(400, "지원하지 않는 소셜 플랫폼입니다."),
  INVALID_ID_TOKEN(400, "ID 토큰 유효성 검사에 실패했습니다."),
  INVALID_PHONE_VERIFICATION_CODE(400, "인증번호가 일치하지 않습니다."),
  ALREADY_REGISTER_PHONE_NUMBER(400, "이미 가입된 전화번호입니다."),
  ALREADY_REGISTERED_SOCIAL_ACCOUNT(400, "이미 가입된 소셜 계정입니다."),
  EXPIRED_PHONE_VERIFICATION(400, "인증 시간이 만료되었습니다."),

  // 401
  INVALID_API_KEY(401, "API 키가 유효하지 않습니다."),
  MISSING_AUTHORIZATION_HEADER(401, "Authorization 헤더가 없습니다."),
  REFRESH_TOKEN_NOT_FOUND(401, "Refresh Token이 없습니다."),

  // 403
  PHONE_NOT_VERIFIED(403, "번호 인증이 되지 않은 번호입니다."),

  // 404
  NOT_FOUND_PHONE_VERIFICATION(404, "존재하지 않는 번호 인증 이력입니다."),
  NOT_FOUND_USER_WITH_SOCIAL_ACCOUNT(400, "소셜 계정 정보와 일치하는 회원이 없습니다."),
  NOT_FOUND_AVAILABLE_PUBLIC_KEY_SET(404, "유효한 Public Key Set를 찾을 수 없습니다."),
  NOT_FOUND_REGISTER_INFO(404, "SOPT 활동 시 사용한 전화번호가 아닙니다."),

  // 500
  SMS_SEND_FAILED(500, "SMS 발송에 실패했습니다.");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
