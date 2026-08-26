package org.sopt.makers.domain.app.operationconfig.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum OperationConfigFailure implements FailureCode {
  NOT_FOUND_OPERATION_CONFIG(404, "운영 설정 값이 존재하지 않습니다.");

  private final int statusCode;
  private final String message;
}
