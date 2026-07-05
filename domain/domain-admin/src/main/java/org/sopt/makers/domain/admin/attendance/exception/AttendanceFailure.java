package org.sopt.makers.domain.admin.attendance.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AttendanceFailure implements FailureCode {
  NOT_FOUND_ATTENDANCE(404, "출석 정보를 찾을 수 없습니다"),
  NOT_FOUND_SUB_ATTENDANCE(404, "세부 출석 정보를 찾을 수 없습니다");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
