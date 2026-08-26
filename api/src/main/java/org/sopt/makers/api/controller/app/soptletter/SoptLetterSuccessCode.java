package org.sopt.makers.api.controller.app.soptletter;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SoptLetterSuccessCode implements SuccessCode {
  GET_ONBOARDING_PROFILE(200, "솝레터 온보딩 프로필 조회에 성공했습니다."),
  COMPLETE_ONBOARDING(200, "솝레터 온보딩 완료 처리에 성공했습니다."),
  GET_REPORT_FORM(200, "솝레터 신고 폼 조회에 성공했습니다."),
  GET_CTA(200, "솝레터 CTA 조회에 성공했습니다."),
  GET_TOPICS(200, "솝레터 주제 목록 조회에 성공했습니다."),
  GET_TOPIC(200, "솝레터 주제 조회에 성공했습니다."),
  GET_TOPIC_MESSAGES(200, "솝레터 메시지 목록 조회에 성공했습니다."),
  GET_MESSAGE(200, "솝레터 메시지 조회에 성공했습니다."),
  WRITE_MESSAGE(201, "솝레터 메시지 작성에 성공했습니다."),
  UPDATE_MESSAGE(200, "솝레터 메시지 수정에 성공했습니다."),
  DELETE_MESSAGE(200, "솝레터 메시지 삭제에 성공했습니다."),
  ADD_LIKE(200, "솝레터 메시지 좋아요에 성공했습니다."),
  REMOVE_LIKE(200, "솝레터 메시지 좋아요 취소에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
