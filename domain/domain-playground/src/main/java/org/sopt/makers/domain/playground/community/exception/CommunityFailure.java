package org.sopt.makers.domain.playground.community.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CommunityFailure implements FailureCode {
  INVALID_FREE_FILTER(400, "자유 카테고리는 filter 값을 받을 수 없습니다."),
  INVALID_PROMOTION_FILTER(400, "홍보 카테고리에서 사용할 수 없는 filter 값입니다."),
  INVALID_SOPTICLE_FILTER(400, "솝티클 카테고리에서 사용할 수 없는 filter 값입니다."),
  NOT_FOUND_CATEGORY(404, "존재하지 않는 category code 값입니다.");

  private final int statusCode;
  private final String message;
}
