package org.sopt.makers.domain.official.homepage.review;

import java.time.LocalDateTime;

public record HomepageReview(
    Long id,
    HomepageReviewType type,
    String title,
    String content,
    String authorInfo,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static HomepageReview create(String title, String content, String authorInfo) {
    return new HomepageReview(
        null, HomepageReviewType.ACTIVITY, title, content, authorInfo, null, null);
  }

  public HomepageReview update(String title, String content, String authorInfo) {
    return new HomepageReview(id, type, title, content, authorInfo, createdAt, updatedAt);
  }
}
