package org.sopt.makers.api.controller.playground.review;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ActivityReviewSuccessCode implements SuccessCode {
  SUCCESS_CREATE_ACTIVITY_REVIEW(201, "활동 후기 생성 성공"),
  SUCCESS_GET_ACTIVITY_REVIEWS(200, "활동 후기 조회 성공");

  private final int statusCode;
  private final String message;
}
