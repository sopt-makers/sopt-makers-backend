package org.sopt.makers.api.controller.crew.soptmap.dto;

import org.sopt.makers.domain.crew.soptmap.service.SoptMapService;

public record ToggleSoptMapRecommendResponse(Long soptMapId, Boolean toggleStatus) {

  public static ToggleSoptMapRecommendResponse from(SoptMapService.ToggleRecommendResult result) {
    return new ToggleSoptMapRecommendResponse(result.soptMapId(), result.isRecommended());
  }
}
