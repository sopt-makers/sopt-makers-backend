package org.sopt.makers.api.common.response;

import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 앱 채널 전환기 전용 에러 응답. 구 앱 서버의 FailureResponse({message, status, errors}) 계약을 보존한다. 통합 레포 표준은
 * BaseResponse이며, 앱 클라이언트가 BaseResponse 파싱으로 전환되면 이 클래스와 AppExceptionHandler를 제거한다.
 */
@Getter
public class AppFailureResponse {

  private final String message;
  // 구 앱 서버는 Jackson 2 기본값(name())으로 "NOT_FOUND" 형태를 내보냈다. Jackson 3은 enum을
  // toString()("404 NOT_FOUND")으로 직렬화하므로, 계약 보존을 위해 name()을 String으로 고정한다.
  private final String status;
  private final List<FieldError> errors;

  private AppFailureResponse(String message, HttpStatus status, List<FieldError> errors) {
    this.message = message;
    this.status = status.name();
    this.errors = errors;
  }

  public static AppFailureResponse of(String message, HttpStatus status) {
    return new AppFailureResponse(message, status, List.of());
  }

  public static AppFailureResponse of(String message, HttpStatus status, List<FieldError> errors) {
    return new AppFailureResponse(message, status, errors);
  }

  /** 구 앱 서버 FailureResponse.FieldError({field, value, reason}) 계약. */
  public record FieldError(String field, String value, String reason) {}
}
