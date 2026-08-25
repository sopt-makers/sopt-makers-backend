package org.sopt.makers.domain.crew.meeting.tag.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MeetingTagFailure implements FailureCode {
  NOT_FOUND_TAG(404, "모임 태그를 찾을 수 없습니다."),
  INVALID_TAG_VALUE(400, "모임 태그 입력 값이 올바르지 않습니다."),
  INVALID_WELCOME_MESSAGE_TYPE(400, "환영 메시지 타입이 올바르지 않습니다."),
  INVALID_MEETING_KEYWORD_TYPE(400, "모임 키워드 타입이 올바르지 않습니다."),
  INVALID_MEETING_KEYWORD_SIZE(400, "모임 키워드는 1개 이상 2개 이하로 입력해야 합니다.");

  private final int statusCode;
  private final String message;
}
