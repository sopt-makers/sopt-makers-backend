package org.sopt.makers.api.controller.app.push.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DeletePushTokenRequest(
    @Schema(description = "해제할 푸시 토큰", example = "fZ1mK...") @NotBlank String pushToken) {}
