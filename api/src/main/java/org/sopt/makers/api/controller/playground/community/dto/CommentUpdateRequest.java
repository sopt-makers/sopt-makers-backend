package org.sopt.makers.api.controller.playground.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(
    @Schema(required = true) @NotBlank(message = "댓글 내용은 필수입니다") String content) {}
