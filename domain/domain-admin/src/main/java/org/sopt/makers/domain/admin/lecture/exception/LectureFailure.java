package org.sopt.makers.domain.admin.lecture.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum LectureFailure implements FailureCode {
  ATTENDANCE_NOT_STARTED(400, "아직 출석 체크가 시작되지 않았습니다"),
  ATTENDANCE_ENDED(400, "출석 체크 시간이 종료되었습니다"),
  INVALID_ATTENDANCE_CODE(400, "출석 코드가 올바르지 않습니다"),
  INVALID_SESSION_COUNT(400, "세션 개수가 올바르지 않습니다"),
  NOT_FOUND_TODAY_LECTURE(404, "오늘 진행되는 세션이 없습니다"),
  LECTURE_NOT_STARTED(400, "세션이 아직 시작되지 않았습니다"),
  LECTURE_ENDED(400, "세션이 종료되었습니다"),
  NOT_FOUND_LECTURE(404, "존재하지 않는 세션입니다"),
  NOT_FOUND_SUB_LECTURE(404, "존재하지 않는 세부 세션입니다"),
  LECTURE_NOT_YET_ENDED(400, "아직 종료 시간이 아닙니다"),
  FIRST_ATTENDANCE_NOT_STARTED(400, "1차 출석이 시작되지 않았습니다"),
  ALREADY_STARTED_ROUND(400, "이미 시작된 출석 차수입니다"),
  INVALID_LECTURE_TIME(400, "세션 시작 시간은 종료 시간보다 이전이어야 합니다"),
  NO_MATCHING_SUB_LECTURE_ROUND(404, "해당 차수의 서브 세션이 없습니다");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
