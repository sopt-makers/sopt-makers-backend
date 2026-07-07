package org.sopt.makers.api.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "소셜 계정 변경 요청")
public record UpdateSocialAccountRequest(
    @NotNull @Schema(description = "전화번호") String phone,
    @NotNull @Schema(description = "소셜 계정 토큰") String token,
    @NotNull @Schema(description = "소셜 플랫폼 (예: APPLE, GOOGLE)") String authPlatform) {}
