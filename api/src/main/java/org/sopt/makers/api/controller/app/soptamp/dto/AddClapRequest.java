package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

public record AddClapRequest(
    @Schema(description = "한 번에 보낼 박수 횟수. 1 이상", example = "5")
        @Positive(message = "clapCount must be > 0")
        int clapCount) {}
