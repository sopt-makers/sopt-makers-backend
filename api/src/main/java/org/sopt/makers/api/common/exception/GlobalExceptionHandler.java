package org.sopt.makers.api.common.exception;

import static org.sopt.makers.api.common.exception.CommonFailureCode.INTERNAL_SERVER_ERROR;
import static org.sopt.makers.api.common.exception.CommonFailureCode.INVALID_INPUT_VALUE;
import static org.sopt.makers.api.common.exception.CommonFailureCode.INVALID_REQUEST_BODY;
import static org.sopt.makers.api.common.exception.CommonFailureCode.METHOD_ARGUMENT_TYPE_MISMATCH;
import static org.sopt.makers.api.common.exception.CommonFailureCode.METHOD_NOT_SUPPORTED;
import static org.sopt.makers.api.common.exception.CommonFailureCode.MISSING_REQUEST_HEADER;
import static org.sopt.makers.api.common.exception.CommonFailureCode.NOT_FOUND_URL;
import static org.sopt.makers.api.common.exception.CommonFailureCode.NO_RESOURCE_FOUND;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.core.code.FailureCode;
import org.sopt.makers.core.exception.BaseException;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String VALIDATION_KEY_FORMAT = "valid_%s";

  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<?>> handleInternalException(final Exception e) {
    log.error(e.getMessage(), e);
    return toResponse(INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<BaseResponse<?>> handleBusinessException(final BaseException e) {
    log.warn(e.getMessage());
    return toResponse(e.getError());
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<BaseResponse<?>> handleNoResourceFoundException(
      final NoResourceFoundException e) {
    log.warn(e.getMessage());
    return toResponse(NO_RESOURCE_FOUND);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<BaseResponse<?>> handleNoHandlerFoundException(
      final NoHandlerFoundException e) {
    log.warn(e.getMessage());
    return toResponse(NOT_FOUND_URL);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<BaseResponse<?>> handleHttpRequestMethodNotSupportedException(
      final HttpRequestMethodNotSupportedException e) {
    log.warn(e.getMessage());
    return toResponse(METHOD_NOT_SUPPORTED);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<BaseResponse<?>> handleTypeMismatch(
      final MethodArgumentTypeMismatchException e) {
    log.warn(e.getMessage());
    return toResponse(METHOD_ARGUMENT_TYPE_MISMATCH);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(
      final MethodArgumentNotValidException e) {
    log.warn(e.getMessage());
    Map<String, String> errorDetails = new HashMap<>();
    for (FieldError error : e.getBindingResult().getFieldErrors()) {
      String message =
          error.getDefaultMessage() != null ? error.getDefaultMessage() : "유효하지 않은 입력값입니다";
      errorDetails.put(String.format(VALIDATION_KEY_FORMAT, error.getField()), message);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.ofFailure(INVALID_INPUT_VALUE, errorDetails));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<BaseResponse<?>> handleConstraintViolationException(
      final ConstraintViolationException e) {
    log.warn(e.getMessage());
    Map<String, String> errorDetails = new HashMap<>();
    e.getConstraintViolations()
        .forEach(
            v -> {
              String paramName = v.getPropertyPath().toString();
              errorDetails.put(String.format(VALIDATION_KEY_FORMAT, paramName), v.getMessage());
            });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.ofFailure(INVALID_INPUT_VALUE, errorDetails));
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<BaseResponse<?>> handleMissingHeaderException(
      final MissingRequestHeaderException e) {
    log.warn(e.getMessage());
    return toResponse(MISSING_REQUEST_HEADER);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<BaseResponse<?>> handleNotReadableException(
      final HttpMessageNotReadableException e) {
    log.warn(e.getMessage());
    return toResponse(INVALID_REQUEST_BODY);
  }

  private ResponseEntity<BaseResponse<?>> toResponse(FailureCode code) {
    return ResponseEntity.status(HttpStatus.valueOf(code.getStatusCode()))
        .body(BaseResponse.ofFailure(code));
  }
}
