package org.sopt.makers.domain.crew.notice.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum NoticeFailure implements FailureCode {
  FORBIDDEN_NOTICE_CREATION(403, "공지 생성 권한이 없습니다."),
  INVALID_NOTICE_VALUE(400, "공지 입력 값이 올바르지 않습니다."),
  INVALID_NOTICE_EXPOSURE_PERIOD(400, "공지 노출 기간이 올바르지 않습니다.");

  private final int statusCode;
  private final String message;
}
