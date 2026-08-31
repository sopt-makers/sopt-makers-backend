package org.sopt.makers.api.controller.crew.user;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MeetingKeywordPreferenceSuccessCode implements SuccessCode {
  UPDATE_MEETING_KEYWORD_PREFERENCE(200, "관심 모임 키워드 설정에 성공했습니다."),
  GET_MEETING_KEYWORD_PREFERENCE(200, "관심 모임 키워드 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
