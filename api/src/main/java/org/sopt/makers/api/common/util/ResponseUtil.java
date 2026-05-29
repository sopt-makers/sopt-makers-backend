package org.sopt.makers.api.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;
import org.sopt.makers.core.code.SuccessCode;
import org.sopt.makers.core.exception.BaseException;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class ResponseUtil {

  private final ObjectMapper objectMapper;

  public void generateErrorResponse(
      final HttpServletResponse response, final BaseException exception) throws IOException {
    String bodyValue =
        objectMapper.writeValueAsString(BaseResponse.ofFailure(exception.getError()));

    response.setStatus(exception.getError().getStatusCode());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.getWriter().write(bodyValue);
  }

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
