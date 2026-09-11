package org.sopt.makers.api.controller.app.home.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.home.FloatingButton;

public record FloatingButtonResponse(
    @Schema(description = "버튼에 띄울 이미지 주소", example = "https://s3.sopt.org/floating/banner.png")
        String imageUrl,
    @Schema(description = "버튼 제목", example = "35기 지원하기") String title,
    @Schema(description = "펼쳤을 때 보이는 부제", example = "9월 19일까지 지원할 수 있어요") String expandedSubTitle,
    @Schema(description = "접었을 때 보이는 부제", example = "지원 마감 D-3") String collapsedSubtitle,
    @Schema(description = "동작 버튼에 띄울 문구", example = "지원하러 가기") String actionButtonName,
    @Schema(description = "눌렀을 때 이동할 주소", example = "https://sopt.org/recruit") String linkUrl,
    @Schema(description = "버튼을 띄울지 여부. 비로그인이면 항상 false", example = "true") @JsonProperty("isActive")
        boolean isActive) {

  public static FloatingButtonResponse of(FloatingButton floatingButton) {
    return new FloatingButtonResponse(
        floatingButton.imageUrl(),
        floatingButton.title(),
        floatingButton.expandedSubTitle(),
        floatingButton.collapsedSubtitle(),
        floatingButton.actionButtonName(),
        floatingButton.linkUrl(),
        floatingButton.isActive());
  }
}
