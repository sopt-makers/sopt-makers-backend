package org.sopt.makers.api.controller.app.home.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.sopt.makers.domain.app.home.FloatingButton;

public record FloatingButtonResponse(
    String imageUrl,
    String title,
    String expandedSubTitle,
    String collapsedSubtitle,
    String actionButtonName,
    String linkUrl,
    @JsonProperty("isActive") boolean isActive) {

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
