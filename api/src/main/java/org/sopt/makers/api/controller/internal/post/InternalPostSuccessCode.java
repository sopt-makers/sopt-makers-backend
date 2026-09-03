package org.sopt.makers.api.controller.internal.post;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum InternalPostSuccessCode implements SuccessCode {
  GET_POSTS(200, "Internal 모임 피드 조회에 성공했습니다."),
  CREATE_POST(201, "Internal 모임 피드 생성에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
