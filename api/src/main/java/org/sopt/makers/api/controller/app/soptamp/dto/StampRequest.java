package org.sopt.makers.api.controller.app.soptamp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public final class StampRequest {

  private StampRequest() {}

  public record FindStampRequest(
      @NotNull(message = "missionId may not be null") Long missionId,
      @NotNull(message = "nickname may not be null") String nickname) {}

  public record RegisterStampRequest(
      @NotNull(message = "missionId may not be null") Long missionId,
      @NotNull(message = "image may not be null") String image,
      @NotNull(message = "contents may not be null")
          @NotEmpty(message = "contents may not be empty")
          String contents,
      @NotNull(message = "activity date may not be null") String activityDate) {}

  public record EditStampRequest(
      @NotNull(message = "missionId may not be null") Long missionId,
      @NotNull(message = "image may not be null") String image,
      @NotNull(message = "contents may not be null")
          @NotEmpty(message = "contents may not be empty")
          String contents,
      @NotNull(message = "activity date may not be null") String activityDate) {}
}
