package org.sopt.makers.api.controller.crew.flash;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum FlashSuccessCode implements SuccessCode {
  CREATE_FLASH(201, "번쩍 모임 생성에 성공했습니다."),
  GET_FLASH(200, "번쩍 모임 상세 조회에 성공했습니다."),
  UPDATE_FLASH(200, "번쩍 모임 수정에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
