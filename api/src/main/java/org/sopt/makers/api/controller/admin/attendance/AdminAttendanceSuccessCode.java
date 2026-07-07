package org.sopt.makers.api.controller.admin.attendance;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AdminAttendanceSuccessCode implements SuccessCode {
  SUCCESS_GET_ATTENDANCES_BY_USER(200, "유저 출석 목록 조회가 완료되었습니다"),
  SUCCESS_GET_ATTENDANCES_BY_LECTURE(200, "세션 출석 목록 조회가 완료되었습니다"),
  SUCCESS_UPDATE_SUB_ATTENDANCE(200, "세부 출석 상태가 업데이트되었습니다"),
  SUCCESS_UPDATE_ATTENDANCE_SCORE(200, "출석 점수가 업데이트되었습니다");

  private final int statusCode;
  private final String message;
}
