package org.sopt.makers.api.controller.playground.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CommunityHitRequest(
    @Schema(required = true) @NotNull(message = "게시글 ID 목록은 필수 입력값입니다.") List<Long> postIdList) {}
