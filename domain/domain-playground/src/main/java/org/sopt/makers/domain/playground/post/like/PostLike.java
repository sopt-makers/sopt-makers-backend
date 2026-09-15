package org.sopt.makers.domain.playground.post.like;

import java.time.LocalDateTime;

public record PostLike(
    Long id, Long postId, Long userId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static PostLike create(Long postId, Long userId) {
    return new PostLike(null, postId, userId, null, null);
  }
}
