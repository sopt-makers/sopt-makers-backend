package org.sopt.makers.api.controller.crew.slack.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteSlackEmojiRequest(@NotBlank String identifiedPwd, @NotBlank String callEmoji) {}
