package org.sopt.makers.api.controller.official.homepage.review.dto;

import java.util.List;
import org.sopt.makers.domain.official.homepage.review.HomepageReview;

public record HomepageReviewResponse(Long id, String title, String content, String authorInfo) {

  public static HomepageReviewResponse of(HomepageReview review) {
    return new HomepageReviewResponse(
        review.id(), review.title(), review.content(), review.authorInfo());
  }

  public static List<HomepageReviewResponse> of(List<HomepageReview> reviews) {
    return reviews.stream().map(HomepageReviewResponse::of).toList();
  }
}
