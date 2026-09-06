package org.sopt.makers.api.controller.app.home.dto;

public record HomeDescriptionResponse(String activityDescription) {

  public static HomeDescriptionResponse of(String activityDescription) {
    return new HomeDescriptionResponse(activityDescription);
  }
}
