package org.sopt.makers.api.controller.app.push.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.sopt.makers.domain.app.push.PushTokenPlatform;

public record RegisterPushTokenRequest(
    @Schema(description = "기기 플랫폼. iOS 또는 Android", example = "iOS") @NotBlank String platform,
    @Schema(description = "기기에서 발급받은 푸시 토큰", example = "fZ1mK...") @NotBlank String pushToken) {

  public PushTokenPlatform toPlatform() {
    return PushTokenPlatform.from(platform);
  }
}
