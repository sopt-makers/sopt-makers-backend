package org.sopt.makers.domain.admin.attendance.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AttendanceFailure implements FailureCode {
  ATTENDANCE_NOT_STARTED(400, "아직 출석 체크가 시작되지 않았습니다"),
  ATTENDANCE_ENDED(400, "출석 체크 시간이 종료되었습니다"),
  INVALID_ATTENDANCE_CODE(400, "출석 코드가 올바르지 않습니다"),
  INVALID_SESSION_COUNT(400, "세션 개수가 올바르지 않습니다"),
  NOT_FOUND_TODAY_LECTURE(404, "오늘 진행되는 세션이 없습니다"),
  LECTURE_NOT_STARTED(400, "세션이 아직 시작되지 않았습니다"),
  LECTURE_ENDED(400, "세션이 종료되었습니다"),
  NOT_FOUND_LECTURE(404, "존재하지 않는 세션입니다"),
  NOT_FOUND_SUB_LECTURE(404, "존재하지 않는 세부 세션입니다"),
  NOT_FOUND_ATTENDANCE(404, "출석 정보를 찾을 수 없습니다"),
  NOT_FOUND_SUB_ATTENDANCE(404, "세부 출석 정보를 찾을 수 없습니다");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
