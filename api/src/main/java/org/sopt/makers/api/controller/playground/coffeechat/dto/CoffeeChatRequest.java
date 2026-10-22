package org.sopt.makers.api.controller.playground.coffeechat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.sopt.makers.domain.playground.coffeechat.enums.ChatCategory;

public record CoffeeChatRequest(
    @NotNull Long receiverId,
    @NotBlank @Pattern(regexp = "^(010|015)\\d{8}$") String senderPhone,
    @NotNull ChatCategory category,
    @NotBlank @Size(max = 500) String content) {}
