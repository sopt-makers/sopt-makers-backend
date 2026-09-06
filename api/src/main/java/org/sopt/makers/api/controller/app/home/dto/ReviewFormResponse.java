package org.sopt.makers.api.controller.app.home.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.sopt.makers.domain.app.home.ReviewForm;

public record ReviewFormResponse(
    String title,
    String subTitle,
    String actionButtonName,
    String linkUrl,
    @JsonProperty("isActive") boolean isActive) {

  public static ReviewFormResponse of(ReviewForm reviewForm) {
    return new ReviewFormResponse(
        reviewForm.title(),
        reviewForm.subTitle(),
        reviewForm.actionButtonName(),
        reviewForm.linkUrl(),
        reviewForm.isActive());
  }
}
