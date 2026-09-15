package org.sopt.makers.api.controller.playground.internal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateDefaultUserProfileRequest(
    @Schema(description = "생성할 유저 ID", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId) {}
