package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record EditProfileMessageRequest(
    @Schema(description = "바꿀 프로필 한마디", example = "오늘도 화이팅")
        @NotNull(message = "profileMessage may not be null")
        String profileMessage) {}
