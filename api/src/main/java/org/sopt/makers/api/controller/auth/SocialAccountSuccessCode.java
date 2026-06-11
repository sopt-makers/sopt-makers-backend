package org.sopt.makers.api.controller.auth;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SocialAccountSuccessCode implements SuccessCode {
  GET_SOCIAL_ACCOUNT_PLATFORM(200, "가입 계정 플랫폼 정보 조회에 성공했습니다."),
  UPDATE_SOCIAL_ACCOUNT(200, "소셜 계정 변경에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
