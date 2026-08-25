package org.sopt.makers.api.controller.admin.auth;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdminAuthSuccessCode implements SuccessCode {
  SUCCESS_LOGIN(200, "어드민 로그인이 완료되었습니다"),
  SUCCESS_REFRESH_TOKEN(200, "토큰 재발급이 완료되었습니다"),
  SUCCESS_CHANGE_PASSWORD(200, "비밀번호 변경이 완료되었습니다");

  private final int statusCode;
  private final String message;
}
