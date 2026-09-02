package org.sopt.makers.api.controller.app.soptamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUserStatus;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;

public final class AppjamUserResponse {

  private AppjamUserResponse() {}

  public record AppjamStatusResponse(
      TeamNumber teamNumber,
      String teamName,
      @JsonProperty("isAppjamJoined") boolean isAppjamJoined) {

    public static AppjamStatusResponse of(AppjamUserStatus status) {
      return new AppjamStatusResponse(
          status.teamNumber(), status.teamName(), status.isAppjamJoined());
    }
  }
}
