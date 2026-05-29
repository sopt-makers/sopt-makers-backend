package org.sopt.makers.api.common.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CommonFailureCode implements FailureCode {

  // 500
  INTERNAL_SERVER_ERROR(500, "서버 내부 오류입니다"),

  // 400
  METHOD_NOT_SUPPORTED(400, "허용되지 않은 메서드입니다"),
  MISSING_REQUEST_HEADER(400, "필수 요청 헤더가 누락되었습니다"),
  INVALID_INPUT_VALUE(400, "유효하지 않은 입력 값입니다"),
  METHOD_ARGUMENT_TYPE_MISMATCH(400, "입력한 값의 타입이 잘못되었습니다"),
  INVALID_REQUEST_BODY(400, "요청 본문을 읽을 수 없습니다"),

  // 404
  NOT_FOUND_URL(404, "존재하지 않는 URL입니다"),
  NO_RESOURCE_FOUND(404, "요청한 리소스를 찾을 수 없습니다");

  private final int statusCode;
  private final String message;
}
