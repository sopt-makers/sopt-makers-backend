package org.sopt.makers.api.controller.playground.review.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateActivityReviewRequest(@NotBlank String content) {}
