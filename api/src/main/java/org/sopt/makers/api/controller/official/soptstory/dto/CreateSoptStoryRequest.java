package org.sopt.makers.api.controller.official.soptstory.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSoptStoryRequest(@NotBlank String link) {}
