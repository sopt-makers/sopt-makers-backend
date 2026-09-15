package org.sopt.makers.api.controller.app.soptamp;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AppjamUserSuccessCode implements SuccessCode {
  GET_APPJAM_INFO(200, "앱잼 팀 정보 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
