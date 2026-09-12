package org.sopt.makers.api.controller.app.poke.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record IsNew(
    @Schema(description = "서로 맞찌른 친구가 한 명도 없는지 여부", example = "true") boolean isNew) {}
