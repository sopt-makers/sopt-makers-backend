package org.sopt.makers.api.controller.app.push.dto;

import jakarta.validation.constraints.NotBlank;

public record DeletePushTokenRequest(@NotBlank String pushToken) {}
