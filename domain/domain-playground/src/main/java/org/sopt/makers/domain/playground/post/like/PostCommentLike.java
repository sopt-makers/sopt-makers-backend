package org.sopt.makers.domain.playground.post.like;

import java.time.LocalDateTime;

public record PostCommentLike(
    Long id, Long commentId, Long userId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static PostCommentLike create(Long commentId, Long userId) {
    return new PostCommentLike(null, commentId, userId, null, null);
  }
}
