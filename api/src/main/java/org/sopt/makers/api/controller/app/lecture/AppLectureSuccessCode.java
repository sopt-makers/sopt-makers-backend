package org.sopt.makers.api.controller.app.lecture;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum AppLectureSuccessCode implements SuccessCode {
  SUCCESS_SINGLE_GET_LECTURE(200, "진행 중인 세션 조회가 완료되었습니다"),
  SUCCESS_GET_LECTURE_ROUND(200, "출석 차수 조회가 완료되었습니다");

  private final int statusCode;
  private final String message;
}
