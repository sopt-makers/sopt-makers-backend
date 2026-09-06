package org.sopt.makers.api.controller.app.user;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AppUserSuccessCode implements SuccessCode {
  GET_MAIN_VIEW(200, "메인 뷰 조회에 성공했습니다."),
  GET_GENERATION(200, "기수 정보 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
