package org.sopt.makers.domain.crew.flash.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum FlashFailure implements FailureCode {
  NOT_FOUND_FLASH(404, "번쩍 모임을 찾을 수 없습니다."),
  FORBIDDEN_FLASH(403, "번쩍 모임에 대한 권한이 없습니다."),
  INVALID_FLASH_VALUE(400, "번쩍 입력 값이 올바르지 않습니다."),
  INVALID_FLASH_CAPACITY(400, "번쩍 모집 인원이 올바르지 않습니다."),
  INVALID_FLASH_DATE(400, "번쩍 활동 기간이 올바르지 않습니다."),
  INVALID_FLASH_PLACE_TYPE(400, "번쩍 장소 타입이 올바르지 않습니다."),
  INVALID_FLASH_TIMING_TYPE(400, "번쩍 일정 타입이 올바르지 않습니다.");

  private final int statusCode;
  private final String message;
}
