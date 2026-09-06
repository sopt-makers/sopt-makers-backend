package org.sopt.makers.api.controller.app.config;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AppConfigSuccessCode implements SuccessCode {
  GET_AVAILABILITY(200, "앱 이용 가능 여부 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
