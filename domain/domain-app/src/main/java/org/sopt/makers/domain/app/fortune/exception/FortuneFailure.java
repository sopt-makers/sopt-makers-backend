package org.sopt.makers.domain.app.fortune.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum FortuneFailure implements FailureCode {
  NOT_FOUND_FORTUNE_WORD(404, "운세 ID에 해당하는 FortuneWord가 없습니다."),
  NOT_FOUND_FORTUNE_FROM_USER(404, "유저에게 할당된 오늘의 운세가 없습니다.");

  private final int statusCode;
  private final String message;
}
