package org.sopt.makers.api.controller.official.review;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ReviewSuccessCode implements SuccessCode {
  CREATE_REVIEW(201, "활동후기 생성에 성공했습니다."),
  GET_REVIEWS(200, "활동후기 목록 조회에 성공했습니다."),
  GET_RANDOM_REVIEWS(200, "파트별 랜덤 활동후기 조회에 성공했습니다."),
  GET_REVIEWS_BY_AUTHOR(200, "작성자별 활동후기 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
