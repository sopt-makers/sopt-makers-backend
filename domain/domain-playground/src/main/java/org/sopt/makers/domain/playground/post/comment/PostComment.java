package org.sopt.makers.domain.playground.post.comment;

import static org.sopt.makers.domain.playground.post.exception.PostFailure.DELETED_COMMENT;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.FORBIDDEN_COMMENT;

import java.time.LocalDateTime;
import java.util.Objects;
import org.sopt.makers.domain.playground.post.exception.PostException;

public record PostComment(
    Long id,
    Long postId,
    Long writerId,
    String contents,
    Long parentCommentId,
    int depth,
    int order,
    int likeCount,
    boolean isDeleted,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  private static final String DELETED_CONTENTS = "삭제된 댓글입니다.";

  public static PostComment createParent(Long postId, Long writerId, String contents) {
    return new PostComment(null, postId, writerId, contents, null, 0, 0, 0, false, null, null);
  }

  public static PostComment createReply(
      Long postId, Long writerId, String contents, Long parentCommentId, int order) {
    return new PostComment(
        null, postId, writerId, contents, parentCommentId, 1, order, 0, false, null, null);
  }

  public boolean isParent() {
    return depth == 0;
  }

  public boolean isWriter(Long userId) {
    return writerId != null && Objects.equals(writerId, userId);
  }

  public void validateWriter(Long userId) {
    if (!isWriter(userId)) {
      throw new PostException(FORBIDDEN_COMMENT);
    }
    validateNotDeleted();
  }

  public PostComment update(String contents) {
    validateNotDeleted();
    return copy(writerId, contents, likeCount, false);
  }

  public PostComment markDeleted() {
    return copy(null, DELETED_CONTENTS, likeCount, true);
  }

  public PostComment increaseLikeCount() {
    validateNotDeleted();
    return copy(writerId, contents, likeCount + 1, false);
  }

  public PostComment decreaseLikeCount() {
    validateNotDeleted();
    return copy(writerId, contents, Math.max(0, likeCount - 1), false);
  }

  public void validateNotDeleted() {
    if (isDeleted) {
      throw new PostException(DELETED_COMMENT);
    }
  }

  private PostComment copy(
      Long nextWriterId, String nextContents, int nextLikeCount, boolean nextDeleted) {
    return new PostComment(
        id,
        postId,
        nextWriterId,
        nextContents,
        parentCommentId,
        depth,
        order,
        nextLikeCount,
        nextDeleted,
        createdAt,
        updatedAt);
  }
}
