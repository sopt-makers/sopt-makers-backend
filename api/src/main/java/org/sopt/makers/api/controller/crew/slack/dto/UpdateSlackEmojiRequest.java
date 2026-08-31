package org.sopt.makers.api.controller.crew.slack.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateSlackEmojiRequest(
    @NotBlank String identifiedPwd,
    @NotBlank String originalCallEmoji,
    @NotBlank String updateCallEmoji) {}
