package org.sopt.makers.api.controller.playground.review.dto;

import org.sopt.makers.domain.playground.review.ActivityReview;

public record ActivityReviewResponse(Long id, String content) {

  public static ActivityReviewResponse from(ActivityReview review) {
    return new ActivityReviewResponse(review.id(), review.content());
  }
}
