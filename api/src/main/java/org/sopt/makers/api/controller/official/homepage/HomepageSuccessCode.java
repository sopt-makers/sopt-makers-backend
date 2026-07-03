package org.sopt.makers.api.controller.official.homepage;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum HomepageSuccessCode implements SuccessCode {
  GET_HOMEPAGE_MAIN(200, "메인 페이지 조회에 성공했습니다."),
  GET_HOMEPAGE_ABOUT(200, "About 페이지 조회에 성공했습니다."),
  GET_HOMEPAGE_RECRUIT(200, "Recruiting 페이지 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
