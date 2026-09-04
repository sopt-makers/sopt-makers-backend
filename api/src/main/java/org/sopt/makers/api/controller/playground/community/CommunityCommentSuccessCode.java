package org.sopt.makers.api.controller.playground.community;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CommunityCommentSuccessCode implements SuccessCode {
  CREATE_COMMENT(201, "댓글 생성 성공"),
  GET_COMMENTS(200, "커뮤니티 댓글 조회 성공"),
  UPDATE_COMMENT(200, "커뮤니티 댓글 수정 성공"),
  DELETE_COMMENT(200, "커뮤니티 댓글 삭제 성공"),
  REPORT_COMMENT(201, "커뮤니티 댓글 신고 성공"),
  LIKE_COMMENT(201, "커뮤니티 댓글 좋아요 성공"),
  UNLIKE_COMMENT(200, "커뮤니티 댓글 좋아요 취소 성공");

  private final int statusCode;
  private final String message;
}
