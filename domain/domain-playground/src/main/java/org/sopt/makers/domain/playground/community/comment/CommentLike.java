package org.sopt.makers.domain.playground.community.comment;

import java.time.LocalDateTime;

public record CommentLike(Long id, Long userId, Long commentId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static CommentLike create(Long userId, Long commentId) {
    return new CommentLike(null, userId, commentId, null, null);
  }
}
