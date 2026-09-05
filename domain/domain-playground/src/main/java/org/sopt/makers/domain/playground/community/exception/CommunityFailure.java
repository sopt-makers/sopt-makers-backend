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
  INVALID_CATEGORY_CODE(400, "유효하지 않은 categoryCode 값입니다."),
  NOT_FOUND_POST(400, "존재하지 않는 postId입니다."),
  INVALID_CURSOR(400, "유효하지 않은 cursor 값입니다."),
  NOT_FOUND_COMMUNITY_POST(404, "존재하지 않는 게시글의 id값 입니다."),
  NOT_FOUND_WRITER(404, "존재하지 않는 사용자의 id값 입니다."),
  UNAUTHORIZED_POST_ACCESS(400, "수정/삭제 권한이 없는 유저입니다."),
  ALREADY_LIKED_POST(400, "이 게시물에는 이미 좋아요를 눌렀습니다."),
  NOT_LIKED_POST(404, "이 게시물에는 아직 좋아요를 누르지 않았습니다."),
  NOT_FOUND_COMMENT(404, "존재하지 않는 댓글의 id값 입니다."),
  UNAUTHORIZED_COMMENT_ACCESS(400, "수정 권한이 없는 유저입니다."),
  ALREADY_DELETED_COMMENT(400, "삭제된 댓글은 수정할 수 없습니다."),
  COMMENT_NOT_BELONGS_TO_POST(400, "해당 게시글의 댓글이 아닙니다."),
  ALREADY_LIKED_COMMENT(400, "이미 좋아요를 누른 댓글입니다."),
  NOT_LIKED_COMMENT(400, "좋아요를 누르지 않은 댓글입니다."),
  NOT_FOUND_ANONYMOUS_NICKNAME_IN_POST(400, "해당 게시글에 존재하지 않는 익명 닉네임입니다."),
  MISSING_PARENT_COMMENT_ID(400, "답글 작성 시 부모 댓글 ID(parentCommentId)는 필수입니다."),
  INVALID_PARENT_COMMENT_ID(400, "일반 댓글 작성 시 부모 댓글 ID(parentCommentId)는 null이어야 합니다."),
  SOPTICLE_SCRAP_FAILED(502, "솝티클 링크의 메타데이터를 가져오는데 실패했습니다."),
  NOT_FOUND_VOTE(404, "해당 게시글에는 투표가 존재하지 않습니다."),
  ALREADY_VOTED(400, "이미 투표했습니다."),
  SOPTICLE_VOTE_NOT_ALLOWED(400, "솝티클 카테고리는 투표를 만들 수 없습니다."),
  INVALID_VOTE_OPTION_COUNT(400, "투표 옵션은 2개 이상 5개 이하만 가능합니다."),
  INVALID_VOTE_OPTION_CONTENT(400, "투표 옵션 내용은 공백일 수 없고 40자까지만 입력 가능합니다."),
  INVALID_VOTE_SELECTION_COUNT(400, "복수 선택 불가능한 투표입니다."),
  INVALID_VOTE_SELECTION_OPTION(400, "존재하지 않는 투표 옵션이 포함되어 있습니다.");

  private final int statusCode;
  private final String message;
}
