package org.sopt.makers.core.response;

import lombok.AccessLevel;
import lombok.Builder;
import org.sopt.makers.core.code.FailureCode;
import org.sopt.makers.core.code.SuccessCode;

@Builder(access = AccessLevel.PRIVATE)
public record BaseResponse<T>(boolean success, String message, T data) {

  public static <T> BaseResponse<T> ofSuccess(final SuccessCode code, final T data) {
    return BaseResponse.<T>builder().success(true).message(code.getMessage()).data(data).build();
  }

  public static BaseResponse<?> ofSuccess(final SuccessCode code) {
    return BaseResponse.builder().success(true).message(code.getMessage()).data(null).build();
  }

  public static <T> BaseResponse<T> ofFailure(final FailureCode code, final T data) {
    return BaseResponse.<T>builder().success(false).message(code.getMessage()).data(data).build();
  }

  public static BaseResponse<?> ofFailure(final FailureCode code) {
    return BaseResponse.builder().success(false).message(code.getMessage()).data(null).build();
  }
}
