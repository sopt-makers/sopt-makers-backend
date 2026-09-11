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

  // TODO(#99): 앱 클라이언트 작업을 위해 스웨거에 응답 타입 명시 필요. 타 팀 응답 형태를 바꾸기 애매해서 별도로 선언함.
  // 추후 타 팀도 타입 형태를 쓰면 이 메서드를 success로 이름 바꿔 승격하고 기존 success는 삭제.
  public static <T> ResponseEntity<BaseResponse<T>> typedSuccess(
      final SuccessCode code, final T data) {
    return ResponseEntity.status(HttpStatus.valueOf(code.getStatusCode()))
        .body(BaseResponse.ofSuccess(code, data));
  }

  public static ResponseEntity<BaseResponse<Void>> typedSuccess(final SuccessCode code) {
    return ResponseEntity.status(HttpStatus.valueOf(code.getStatusCode()))
        .body(BaseResponse.ofSuccess(code, null));
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
