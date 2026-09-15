package org.sopt.makers.domain.playground.post.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum PostFailure implements FailureCode {
  NOT_FOUND_POST(404, "게시글을 찾을 수 없습니다."),
  NOT_FOUND_COMMENT(404, "댓글을 찾을 수 없습니다."),
  NOT_FOUND_MEETING(404, "모임을 찾을 수 없습니다."),
  NOT_FOUND_USER(404, "사용자를 찾을 수 없습니다."),

  FORBIDDEN_MEETING_POST(403, "모임 게시글에 대한 권한이 없습니다."),
  FORBIDDEN_POST(403, "게시글에 대한 권한이 없습니다."),
  FORBIDDEN_COMMENT(403, "댓글에 대한 권한이 없습니다."),

  INVALID_POST_CATEGORY(400, "게시글 카테고리와 모임 정보가 올바르지 않습니다."),
  INVALID_POST_CONTENT_TYPE(400, "게시글 콘텐츠 유형이 올바르지 않습니다."),
  INVALID_COMMENT_PARENT(400, "부모 댓글 정보가 올바르지 않습니다."),
  DELETED_COMMENT(400, "삭제된 댓글은 변경할 수 없습니다."),
  TOO_MANY_POST_IMAGES(400, "이미지는 최대 10개까지 업로드할 수 있습니다."),
  ALREADY_REPORTED_POST(400, "이미 신고한 게시글입니다."),
  ALREADY_REPORTED_COMMENT(400, "이미 신고한 댓글입니다.");

  private final int statusCode;
  private final String message;
}
