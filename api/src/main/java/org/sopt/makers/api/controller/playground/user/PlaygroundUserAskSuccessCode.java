package org.sopt.makers.api.controller.playground.user;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum PlaygroundUserAskSuccessCode implements SuccessCode {
  CREATE_ASK(201, "질문 작성 성공"),
  UPDATE_ASK(200, "질문 수정 성공"),
  DELETE_ASK(200, "질문 삭제 성공"),
  CREATE_ANSWER(201, "답변 작성 성공"),
  UPDATE_ANSWER(200, "답변 수정 성공"),
  DELETE_ANSWER(200, "답변 삭제 성공"),
  TOGGLE_ASK_REACTION(200, "나도 궁금해요 토글 성공"),
  TOGGLE_ANSWER_REACTION(200, "도움돼요 토글 성공"),
  REPORT_ASK(201, "질문 신고 성공"),
  GET_ASKS(200, "질문 목록 조회 성공"),
  GET_UNANSWERED_COUNT(200, "답변 대기 중인 질문 개수 조회 성공"),
  GET_MY_LATEST_ANSWERED_ASK_LOCATION(200, "내 질문의 답변 위치 조회 성공"),
  GET_ASK_LOCATION(200, "질문 위치 조회 성공"),
  GET_LATEST_ANSWERED_ASKS(200, "최신 질문 5개 조회 성공");

  private final int statusCode;
  private final String message;
}
