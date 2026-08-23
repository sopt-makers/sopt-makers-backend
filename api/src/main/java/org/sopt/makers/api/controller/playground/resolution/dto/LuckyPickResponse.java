package org.sopt.makers.api.controller.playground.resolution.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LuckyPickResponse(
    @Schema(description = "당첨 여부", example = "true") boolean isWinner) {}
