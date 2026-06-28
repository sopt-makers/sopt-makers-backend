package org.sopt.makers.api.controller.app.member;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AppMemberSuccessCode implements SuccessCode {
  SUCCESS_GET_TOTAL_ATTENDANCE(200, "전체 출석 정보 조회가 완료되었습니다"),
  SUCCESS_GET_ATTENDANCE_SCORE(200, "출석 점수 조회가 완료되었습니다");

  private final int statusCode;
  private final String message;
}
