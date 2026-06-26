package org.sopt.makers.domain.official.project.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ProjectFailure implements FailureCode {
  PROJECT_NOT_FOUND(404, "프로젝트를 찾을 수 없습니다.");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
