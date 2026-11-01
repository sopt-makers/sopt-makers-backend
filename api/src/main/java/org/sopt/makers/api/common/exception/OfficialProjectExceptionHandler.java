package org.sopt.makers.api.common.exception;

import java.util.Map;
import org.sopt.makers.api.controller.official.project.ProjectController;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ProjectController.class)
public class OfficialProjectExceptionHandler {
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<BaseResponse<?>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    return ResponseEntity.badRequest()
        .body(
            BaseResponse.ofFailure(
                CommonFailureCode.METHOD_ARGUMENT_TYPE_MISMATCH,
                Map.of("valid_" + e.getName(), "유효하지 않은 입력값입니다")));
  }
}
