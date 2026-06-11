package org.sopt.makers.api.controller.auth;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AuthSuccessCode implements SuccessCode {
  CREATE_PHONE_VERIFICATION(201, "번호 인증 생성에 성공했습니다."),
  VERIFY_PHONE_VERIFICATION(200, "번호 인증에 성공했습니다."),
  LOGIN(200, "소셜 로그인에 성공했습니다."),
  SIGNUP(201, "회원 가입에 성공했습니다."),
  REFRESH_TOKEN(200, "토큰 갱신에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
