package org.sopt.makers.api.controller.app.config.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AvailabilityResponse(@JsonProperty("isAvailable") boolean isAvailable) {

  public static AvailabilityResponse of(boolean isAvailable) {
    return new AvailabilityResponse(isAvailable);
  }
}
