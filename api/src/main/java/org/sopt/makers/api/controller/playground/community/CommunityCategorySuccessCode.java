package org.sopt.makers.api.controller.playground.community;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CommunityCategorySuccessCode implements SuccessCode {
  GET_ALL_CATEGORIES(200, "커뮤니티 카테고리 전체 조회 성공");

  private final int statusCode;
  private final String message;
}
