package org.sopt.makers.domain.playground.community.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum CommunityFailure implements FailureCode {
  INVALID_FREE_FILTER(400, "자유 카테고리는 filter 값을 받을 수 없습니다."),
  INVALID_PROMOTION_FILTER(400, "홍보 카테고리에서 사용할 수 없는 filter 값입니다."),
  INVALID_SOPTICLE_FILTER(400, "솝티클 카테고리에서 사용할 수 없는 filter 값입니다."),
  NOT_FOUND_CATEGORY(404, "존재하지 않는 category code 값입니다."),
  NOT_FOUND_ANONYMOUS_NICKNAME(404, "존재하지 않는 익명 닉네임입니다."),
  MISSING_CATEGORY_PARAMETER(400, "categoryCode 또는 category 값이 필요합니다."),
  NOT_FOUND_POST(400, "존재하지 않는 postId입니다."),
  INVALID_CURSOR(400, "유효하지 않은 cursor 값입니다."),
  NOT_FOUND_COMMUNITY_POST(404, "존재하지 않는 게시글의 id값 입니다."),
  NOT_FOUND_WRITER(404, "존재하지 않는 사용자의 id값 입니다."),
  UNAUTHORIZED_POST_ACCESS(400, "수정/삭제 권한이 없는 유저입니다."),
  ALREADY_LIKED_POST(400, "이 게시물에는 이미 좋아요를 눌렀습니다."),
  NOT_LIKED_POST(404, "이 게시물에는 아직 좋아요를 누르지 않았습니다.");

  private final int statusCode;
  private final String message;
}
