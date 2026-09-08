package org.sopt.makers.domain.playground.community.post;

import java.time.LocalDateTime;

public record PostLike(
    Long id, Long userId, Long postId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static PostLike create(Long userId, Long postId) {
    return new PostLike(null, userId, postId, null, null);
  }
}
