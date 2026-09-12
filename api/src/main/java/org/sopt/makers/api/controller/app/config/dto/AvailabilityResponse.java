package org.sopt.makers.api.controller.app.config.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record AvailabilityResponse(
    @Schema(description = "앱 이용 가능 여부. false면 클라이언트가 점검 화면으로 분기한다", example = "true")
        @JsonProperty("isAvailable")
        boolean isAvailable) {

  public static AvailabilityResponse of(boolean isAvailable) {
    return new AvailabilityResponse(isAvailable);
  }
}
