package org.sopt.makers.api.controller.official.homepage.news;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum NewsSuccessCode implements SuccessCode {
  CREATE_NEWS(201, "최신소식 추가에 성공했습니다."),
  UPDATE_NEWS(200, "최신소식 수정에 성공했습니다."),
  DELETE_NEWS(200, "최신소식 삭제에 성공했습니다."),
  GET_NEWS(200, "최신소식 조회에 성공했습니다."),
  GET_NEWS_LIST(200, "최신소식 목록 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
