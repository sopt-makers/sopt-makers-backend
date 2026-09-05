package org.sopt.makers.api.controller.app.soptamp.dto;

import jakarta.validation.constraints.NotNull;

public final class MissionRequest {

  private MissionRequest() {}

  public record RegisterMissionRequest(
      @NotNull(message = "image may not be null") String image,
      @NotNull(message = "title may not be null") String title,
      @NotNull(message = "level may not be null") Integer level) {}
}
