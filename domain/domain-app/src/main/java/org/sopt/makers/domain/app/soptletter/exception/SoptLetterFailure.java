package org.sopt.makers.domain.app.soptletter.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SoptLetterFailure implements FailureCode {
  INVALID_TOPIC_TYPE(400, "지원하지 않는 주제 유형입니다."),
  INVALID_PAGE_SIZE(400, "조회 개수는 1 이상이어야 합니다."),
  DAILY_MESSAGE_LIMIT_EXCEEDED(400, "하루에 작성할 수 있는 메시지 수를 초과했습니다."),
  FORBIDDEN_SOPT_LETTER(403, "본인이 작성한 메시지가 아닙니다."),
  NOT_FOUND_SOPT_LETTER(404, "존재하지 않는 메시지입니다."),
  NOT_FOUND_SOPT_LETTER_TOPIC(404, "존재하지 않는 주제입니다."),
  NOT_FOUND_SOPT_LETTER_PROFILE(404, "솝레터 프로필이 존재하지 않습니다."),
  NICKNAME_IS_FULL(409, "사용할 수 있는 익명 닉네임을 찾지 못했습니다."),
  DUPLICATE_SOPT_LETTER_PROFILE(409, "이미 생성된 솝레터 프로필입니다.");

  private final int statusCode;
  private final String message;
}
