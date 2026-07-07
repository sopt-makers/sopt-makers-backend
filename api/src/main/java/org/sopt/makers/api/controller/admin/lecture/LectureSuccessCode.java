package org.sopt.makers.api.controller.admin.lecture;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum LectureSuccessCode implements SuccessCode {
  SUCCESS_CREATE_LECTURE(201, "세션 생성이 완료되었습니다"),
  SUCCESS_GET_LECTURES(200, "세션 리스트 조회가 완료되었습니다"),
  SUCCESS_GET_LECTURE(200, "세션 단일 조회가 완료되었습니다"),
  SUCCESS_START_ATTENDANCE(200, "출석이 시작되었습니다"),
  SUCCESS_END_LECTURE(200, "세션이 종료되었습니다"),
  SUCCESS_DELETE_LECTURE(200, "세션이 삭제되었습니다"),
  SUCCESS_GET_LECTURE_DETAIL(200, "세션 팝업용 상세 조회가 완료되었습니다");

  private final int statusCode;
  private final String message;
}
