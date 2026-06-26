package org.sopt.makers.api.controller.official.soptstory;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SoptStorySuccessCode implements SuccessCode {
  CREATE_SOPT_STORY(201, "솝트스토리 생성에 성공했습니다."),
  LIKE_SOPT_STORY(200, "솝트스토리 좋아요에 성공했습니다."),
  UNLIKE_SOPT_STORY(200, "솝트스토리 좋아요 취소에 성공했습니다."),
  GET_SOPT_STORIES(200, "솝트스토리 목록 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
