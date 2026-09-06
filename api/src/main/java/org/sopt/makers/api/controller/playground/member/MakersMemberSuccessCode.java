package org.sopt.makers.api.controller.playground.member;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MakersMemberSuccessCode implements SuccessCode {
  GET_MAKERS_PROFILES(200, "메이커스 만든 사람들 조회 성공");

  private final int statusCode;
  private final String message;
}
