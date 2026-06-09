package org.sopt.makers.api.controller.user;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum UserSuccessCode implements SuccessCode {
  GET_USER_PROFILE(200, "유저 기본 정보 조회에 성공했습니다."),
  UPDATE_USER_PROFILE(200, "유저 기본 정보 수정에 성공했습니다."),
  GET_USER_COUNT(200, "유저 수 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
