package org.sopt.makers.api.controller.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record GetSocialAccountPlatformRequest(
    @NotBlank(message = "전화번호는 필수 입력 값입니다.") @JsonProperty("phone") String phone) {}
