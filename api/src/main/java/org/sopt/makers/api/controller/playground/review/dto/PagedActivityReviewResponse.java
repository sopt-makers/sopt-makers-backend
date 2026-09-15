package org.sopt.makers.api.controller.playground.review.dto;

import java.util.List;
import org.sopt.makers.domain.playground.review.port.ActivityReviewRepositoryPort.ActivityReviewPage;

public record PagedActivityReviewResponse(List<ActivityReviewResponse> reviews, Boolean hasNext) {

  public static PagedActivityReviewResponse from(ActivityReviewPage page) {
    return new PagedActivityReviewResponse(
        page.reviews().stream().map(ActivityReviewResponse::from).toList(), page.hasNext());
  }
}
