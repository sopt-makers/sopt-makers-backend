package org.sopt.makers.api.controller.crew.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentRequest(@NotBlank String contents) {}
