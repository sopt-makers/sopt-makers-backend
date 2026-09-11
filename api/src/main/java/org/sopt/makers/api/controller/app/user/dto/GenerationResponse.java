package org.sopt.makers.api.controller.app.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.home.UserActiveInfo;

public record GenerationResponse(
    @Schema(description = "서버가 보는 현재 기수", example = "35") Long currentGeneration,
    @Schema(description = "활동 상태. ACTIVE, INACTIVE, UNAUTHENTICATED", example = "ACTIVE")
        String status) {

  public static GenerationResponse of(UserActiveInfo userActiveInfo) {
    return new GenerationResponse(
        userActiveInfo.currentGeneration(), userActiveInfo.status().name());
  }
}
