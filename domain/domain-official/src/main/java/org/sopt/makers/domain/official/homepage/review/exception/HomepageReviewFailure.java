package org.sopt.makers.domain.official.homepage.review.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum HomepageReviewFailure implements FailureCode {
  NOT_FOUND_HOMEPAGE_REVIEW(404, "존재하지 않는 홈페이지 리뷰입니다"),
  EXCEEDED_HOMEPAGE_REVIEW_COUNT(400, "HomepageReview 개수가 최대 허용치를 초과했습니다");

  private final int statusCode;
  private final String message;

  @Override
  public int getStatusCode() {
    return statusCode;
  }
}
