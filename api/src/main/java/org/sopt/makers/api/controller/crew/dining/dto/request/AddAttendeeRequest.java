package org.sopt.makers.api.controller.crew.dining.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddAttendeeRequest(
    @Schema(description = "추가할 참석자의 크루 사용자 ID", example = "1") @NotNull @Positive Long userId) {}
