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
    return create(HomepageReviewType.ACTIVITY, title, content, authorInfo);
  }

  public HomepageReview update(String title, String content, String authorInfo) {
    return new HomepageReview(id, type, title, content, authorInfo, createdAt, updatedAt);
  }

  public static HomepageReview create(
      HomepageReviewType type, String title, String content, String authorInfo) {
    return new HomepageReview(
        null,
        type == null ? HomepageReviewType.ACTIVITY : type,
        title,
        content,
        authorInfo,
        null,
        null);
  }

  public HomepageReview update(
      HomepageReviewType newType, String title, String content, String authorInfo) {
    return new HomepageReview(
        id, newType == null ? type : newType, title, content, authorInfo, createdAt, updatedAt);
  }
}
