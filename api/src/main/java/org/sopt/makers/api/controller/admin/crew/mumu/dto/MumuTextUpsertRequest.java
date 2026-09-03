package org.sopt.makers.api.controller.admin.crew.mumu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.sopt.makers.domain.playground.post.service.MumuTextService;

public record MumuTextUpsertRequest(
    @NotBlank String text,
    @NotBlank String category,
    @NotNull LocalDateTime showStartDate,
    @NotNull LocalDateTime showEndDate) {

  public MumuTextService.CreateMumuTextCommand toCommand() {
    return new MumuTextService.CreateMumuTextCommand(text, category, showStartDate, showEndDate);
  }
}
