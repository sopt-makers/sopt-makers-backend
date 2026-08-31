package org.sopt.makers.api.controller.app.push.dto;

import jakarta.validation.constraints.NotBlank;
import org.sopt.makers.domain.app.push.PushTokenPlatform;

public record RegisterPushTokenRequest(@NotBlank String platform, @NotBlank String pushToken) {

  public PushTokenPlatform toPlatform() {
    return PushTokenPlatform.from(platform);
  }
}
