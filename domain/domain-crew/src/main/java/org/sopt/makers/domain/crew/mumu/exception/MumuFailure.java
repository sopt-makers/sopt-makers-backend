package org.sopt.makers.domain.crew.mumu.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MumuFailure implements FailureCode {
  NOT_FOUND_MUMU_TEXT(404, "무무 텍스트를 찾을 수 없습니다."),
  OVERLAPPED_MUMU_TEXT_PERIOD(400, "무무 텍스트 노출 기간이 겹칩니다."),
  INVALID_MUMU_TEXT_PERIOD(400, "무무 텍스트 노출 기간이 올바르지 않습니다.");

  private final int statusCode;
  private final String message;
}
