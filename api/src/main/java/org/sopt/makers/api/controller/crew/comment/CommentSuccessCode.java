package org.sopt.makers.api.controller.crew.comment;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CommentSuccessCode implements SuccessCode {
  CREATE_COMMENT(201, "모임 게시글 댓글 생성에 성공했습니다."),
  GET_COMMENTS(200, "모임 게시글 댓글 조회에 성공했습니다."),
  UPDATE_COMMENT(200, "모임 게시글 댓글 수정에 성공했습니다."),
  DELETE_COMMENT(200, "모임 게시글 댓글 삭제에 성공했습니다."),
  REPORT_COMMENT(201, "모임 게시글 댓글 신고에 성공했습니다."),
  TOGGLE_COMMENT_LIKE(201, "모임 게시글 댓글 좋아요 상태 변경에 성공했습니다."),
  MENTION_COMMENT_USERS(200, "모임 게시글 댓글 멘션 알림 전송에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
