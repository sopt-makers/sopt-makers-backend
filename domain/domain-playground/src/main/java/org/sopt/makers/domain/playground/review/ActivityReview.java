package org.sopt.makers.domain.playground.review;

import java.time.LocalDateTime;

public record ActivityReview(
    Long id, Long userId, String content, int generation, LocalDateTime createdAt) {

  public static ActivityReview create(Long userId, String content, int generation) {
    return new ActivityReview(null, userId, content, generation, null);
  }
}
