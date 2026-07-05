package org.sopt.makers.api.controller.admin.user;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdminUserSuccessCode implements SuccessCode {
  SUCCESS_GET_USERS(200, "유저 목록 조회가 완료되었습니다");

  private final int statusCode;
  private final String message;
}
