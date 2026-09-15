package org.sopt.makers.api.controller.playground.internal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

public record InternalRecommendMemberListResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Set<Long> userIds) {}
