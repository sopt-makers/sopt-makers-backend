package org.sopt.makers.api.controller.app.soptamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUserStatus;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;

public final class AppjamUserResponse {

  private AppjamUserResponse() {}

  public record AppjamStatusResponse(
      @Schema(description = "소속 팀 번호. 참여하지 않으면 null") TeamNumber teamNumber,
      @Schema(description = "소속 팀 이름. 참여하지 않으면 null", example = "1팀") String teamName,
      @Schema(description = "앱잼에 참여 중인지 여부", example = "true") @JsonProperty("isAppjamJoined")
          boolean isAppjamJoined) {

    public static AppjamStatusResponse of(AppjamUserStatus status) {
      return new AppjamStatusResponse(
          status.teamNumber(), status.teamName(), status.isAppjamJoined());
    }
  }
}
