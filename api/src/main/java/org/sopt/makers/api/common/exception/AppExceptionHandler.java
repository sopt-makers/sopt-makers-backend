package org.sopt.makers.api.common.exception;

import java.util.List;
import org.sopt.makers.api.common.response.AppFailureResponse;
import org.sopt.makers.api.common.response.AppFailureResponse.FieldError;
import org.sopt.makers.core.exception.BaseException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 앱 채널(controller/app) 전환기 전용 예외 핸들러. 전역 GlobalExceptionHandler(BaseResponse)보다 우선해, 앱 채널만 구 앱 서버의
 * FailureResponse 형식으로 에러를 내보낸다(전환기 계약 보존). 앱 클라이언트가 BaseResponse로 전환되면 제거한다.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "org.sopt.makers.api.controller.app")
public class AppExceptionHandler {

  /** 구 앱 서버 ErrorCode.INVALID_PARAMETER 메시지. */
  private static final String INVALID_PARAMETER_MESSAGE = "잘못된 파라미터 입니다.";

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<AppFailureResponse> handleBaseException(BaseException e) {
    HttpStatus status = HttpStatus.valueOf(e.getError().getStatusCode());
    return ResponseEntity.status(status)
        .body(AppFailureResponse.of(e.getError().getMessage(), status));
  }

  /** 구 앱 서버 GlobalExceptionHandler.handleTypeMismatch 계약: INVALID_PARAMETER + FieldError[]. */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<AppFailureResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException e) {
    String value = e.getValue() == null ? "" : e.getValue().toString();
    List<FieldError> errors = List.of(new FieldError(e.getName(), value, e.getErrorCode()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(AppFailureResponse.of(INVALID_PARAMETER_MESSAGE, HttpStatus.BAD_REQUEST, errors));
  }
}
