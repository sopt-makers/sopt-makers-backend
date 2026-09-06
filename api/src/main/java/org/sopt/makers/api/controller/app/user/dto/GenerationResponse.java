package org.sopt.makers.api.controller.app.user.dto;

import org.sopt.makers.domain.app.home.UserActiveInfo;

public record GenerationResponse(Long currentGeneration, String status) {

  public static GenerationResponse of(UserActiveInfo userActiveInfo) {
    return new GenerationResponse(
        userActiveInfo.currentGeneration(), userActiveInfo.status().name());
  }
}
