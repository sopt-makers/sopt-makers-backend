package org.sopt.makers.api.controller.official.homepage.review.dto;

import java.util.List;
import org.sopt.makers.domain.official.homepage.review.HomepageReview;
import org.sopt.makers.domain.official.homepage.review.HomepageReviewType;

public record HomepageReviewResponse(
    Long id, HomepageReviewType type, String title, String content, String authorInfo) {

  public static HomepageReviewResponse of(HomepageReview review) {
    return new HomepageReviewResponse(
        review.id(), review.type(), review.title(), review.content(), review.authorInfo());
  }

  public static List<HomepageReviewResponse> of(List<HomepageReview> reviews) {
    return reviews.stream().map(HomepageReviewResponse::of).toList();
  }
}
