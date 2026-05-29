package org.sopt.makers.api.common.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sopt.makers.core.code.FailureCode;
import org.sopt.makers.core.code.SuccessCode;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseFactory {

  public static <T> ResponseEntity<BaseResponse<?>> success(final SuccessCode code, final T data) {
    return ResponseEntity.status(HttpStatus.valueOf(code.getStatusCode()))
        .body(BaseResponse.ofSuccess(code, data));
  }

  public static ResponseEntity<BaseResponse<?>> success(final SuccessCode code) {
    return ResponseEntity.status(HttpStatus.valueOf(code.getStatusCode()))
        .body(BaseResponse.ofSuccess(code));
  }

  public static <T> ResponseEntity<BaseResponse<?>> success(
      final SuccessCode code, final HttpHeaders headers, final T data) {
    return ResponseEntity.status(HttpStatus.valueOf(code.getStatusCode()))
        .headers(headers)
        .body(BaseResponse.ofSuccess(code, data));
  }

  public static <T> ResponseEntity<BaseResponse<?>> failure(final FailureCode code, final T data) {
    return ResponseEntity.status(HttpStatus.valueOf(code.getStatusCode()))
        .body(BaseResponse.ofFailure(code, data));
  }

  public static ResponseEntity<BaseResponse<?>> failure(final FailureCode code) {
    return ResponseEntity.status(HttpStatus.valueOf(code.getStatusCode()))
        .body(BaseResponse.ofFailure(code));
  }
}
