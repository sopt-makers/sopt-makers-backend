package org.sopt.makers.domain.official.review.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum ReviewFailure implements FailureCode {
  DUPLICATE_REVIEW_URL(400, "이미 등록된 활동후기입니다"),
  INVALID_CONTENT(400, "유효하지 않은 활동후기 내용입니다"),
  INVALID_AUTHOR(400, "유효하지 않은 활동후기 작성자입니다"),
  INVALID_CATEGORY(400, "유효하지 않은 활동후기 카테고리입니다"),
  INVALID_SUBJECT(400, "유효하지 않은 활동후기 세부 주제입니다"),
  INVALID_URL(400, "유효하지 않은 활동후기 URL입니다"),
  INVALID_GENERATION(400, "유효하지 않은 활동후기 기수입니다"),
  INVALID_PART(400, "유효하지 않은 활동후기 파트입니다"),
  SCRAP_FAILED(500, "활동후기 스크래핑에 실패했습니다");

  private final int statusCode;
  private final String message;
}
