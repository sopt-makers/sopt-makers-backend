package org.sopt.makers.api.controller.official.soptstory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSoptStoryRequest(@NotBlank @Size(max = 500) String link) {}
