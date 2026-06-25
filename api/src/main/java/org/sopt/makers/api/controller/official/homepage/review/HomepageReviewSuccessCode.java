package org.sopt.makers.api.controller.official.homepage.review;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum HomepageReviewSuccessCode implements SuccessCode {
  CREATE_HOMEPAGE_REVIEW(201, "홈페이지 리뷰 추가에 성공했습니다."),
  GET_HOMEPAGE_REVIEWS(200, "홈페이지 리뷰 목록 조회에 성공했습니다."),
  UPDATE_HOMEPAGE_REVIEW(200, "홈페이지 리뷰 수정에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
