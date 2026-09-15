package org.sopt.makers.api.controller.app.soptamp;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AppjamtampSuccessCode implements SuccessCode {
  GET_APPJAM_MISSIONS(200, "앱잼탬프 미션 목록 조회에 성공했습니다."),
  GET_APPJAM_STAMP(200, "앱잼탬프 스탬프 조회에 성공했습니다."),
  REGISTER_APPJAM_STAMP(200, "앱잼탬프 스탬프 제출에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
