package org.sopt.makers.api.controller.internal.dto;

import jakarta.validation.constraints.NotBlank;

public class InternalScrapRequest {

  public record Scrap(@NotBlank String link) {}
}
