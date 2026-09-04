package org.sopt.makers.domain.playground.review.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ActivityReviewFailure implements FailureCode {
  NOT_CURRENT_GENERATION(400, "현재 기수 멤버만 활동 후기를 작성할 수 있습니다");

  private final int statusCode;
  private final String message;
}
