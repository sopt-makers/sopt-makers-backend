package org.sopt.makers.api.controller.app.home;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum HomeSuccessCode implements SuccessCode {
  GET_HOME_DESCRIPTION(200, "홈 설명 조회에 성공했습니다."),
  GET_HOME_APP_SERVICES(200, "홈 앱 서비스 조회에 성공했습니다."),
  GET_TAB_APP_SERVICES(200, "탭 앱 서비스 조회에 성공했습니다."),
  GET_RECENT_POSTS(200, "최신 게시글 조회에 성공했습니다."),
  GET_POPULAR_POSTS(200, "인기 게시글 조회에 성공했습니다."),
  GET_FLOATING_BUTTON(200, "플로팅 버튼 조회에 성공했습니다."),
  GET_REVIEW_FORM(200, "후기 폼 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
