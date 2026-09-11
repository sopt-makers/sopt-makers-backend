package org.sopt.makers.api.controller.app.home.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.home.ReviewForm;

public record ReviewFormResponse(
    @Schema(description = "후기 폼 제목", example = "1차 세미나 후기를 남겨주세요") String title,
    @Schema(description = "후기 폼 부제", example = "3분이면 충분해요") String subTitle,
    @Schema(description = "동작 버튼에 띄울 문구", example = "후기 남기러 가기") String actionButtonName,
    @Schema(description = "눌렀을 때 이동할 주소", example = "https://forms.gle/example") String linkUrl,
    @Schema(description = "후기 폼을 띄울지 여부. 비로그인이면 항상 false", example = "true")
        @JsonProperty("isActive")
        boolean isActive) {

  public static ReviewFormResponse of(ReviewForm reviewForm) {
    return new ReviewFormResponse(
        reviewForm.title(),
        reviewForm.subTitle(),
        reviewForm.actionButtonName(),
        reviewForm.linkUrl(),
        reviewForm.isActive());
  }
}
