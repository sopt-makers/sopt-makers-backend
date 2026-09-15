package org.sopt.makers.api.controller.playground.community;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CommunitySuccessCode implements SuccessCode {
  GET_POST(200, "커뮤니티 글 상세 조회 성공"),
  GET_POSTS(200, "커뮤니티 글 목록 조회 성공"),
  GET_POPULAR_POSTS(200, "커뮤니티 홈 인기글 조회 성공"),
  GET_RECENT_SOPTICLE_POSTS(200, "커뮤니티 홈 최근 솝티클 목록 조회 성공"),
  GET_RECENT_POSTS(200, "커뮤니티 홈 모든 카테고리 최신글 조회 성공"),
  GET_TODAY_HOT_POST(200, "핫 게시물 조회 성공"),
  HIT_POST(200, "커뮤니티 글 조회수 증가 성공"),
  CREATE_POST(201, "커뮤니티 글 생성 성공"),
  UPDATE_POST(200, "커뮤니티 글 수정 성공"),
  DELETE_POST(200, "커뮤니티 글 삭제 성공"),
  LIKE_POST(201, "커뮤니티 게시글 좋아요 성공"),
  UNLIKE_POST(200, "커뮤니티 게시글 좋아요 취소 성공"),
  REPORT_POST(201, "커뮤니티 글 신고 성공"),
  SELECT_VOTE(200, "커뮤니티 게시글 투표 선택 성공");

  private final int statusCode;
  private final String message;
}
