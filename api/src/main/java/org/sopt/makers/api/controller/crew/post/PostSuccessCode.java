package org.sopt.makers.api.controller.crew.post;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum PostSuccessCode implements SuccessCode {
  CREATE_POST(201, "모임 게시글 생성에 성공했습니다."),
  GET_POSTS(200, "모임 게시글 목록 조회에 성공했습니다."),
  GET_POST(200, "모임 게시글 조회에 성공했습니다."),
  GET_POST_COUNT(200, "모임 게시글 개수 조회에 성공했습니다."),
  UPDATE_POST(200, "모임 게시글 수정에 성공했습니다."),
  REPORT_POST(201, "모임 게시글 신고에 성공했습니다."),
  TOGGLE_POST_LIKE(201, "모임 게시글 좋아요 상태 변경에 성공했습니다."),
  INCREASE_POST_VIEW_COUNT(200, "모임 게시글 조회수 증가에 성공했습니다."),
  GET_MUMU_HOME(200, "무무 피드 홈 조회에 성공했습니다."),
  GET_MUMU_TEXT(200, "무무 텍스트 조회에 성공했습니다."),
  MENTION_POST_USERS(200, "모임 게시글 멘션 알림 전송에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
