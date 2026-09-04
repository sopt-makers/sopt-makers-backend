package org.sopt.makers.domain.playground.community.comment;

import java.time.LocalDateTime;

public record CommentLike(Long id, Long memberId, Long commentId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static CommentLike create(Long memberId, Long commentId) {
    return new CommentLike(null, memberId, commentId, null, null);
  }
}
