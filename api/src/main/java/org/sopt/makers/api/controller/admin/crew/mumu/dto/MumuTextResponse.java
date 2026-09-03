package org.sopt.makers.api.controller.admin.crew.mumu.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.playground.post.mumu.MumuText;

public record MumuTextResponse(
    Long id,
    String text,
    String category,
    LocalDateTime showStartDate,
    LocalDateTime showEndDate,
    MumuTextStatus status) {

  public static MumuTextResponse from(MumuText mumuText, LocalDateTime now) {
    return new MumuTextResponse(
        mumuText.id(),
        mumuText.text(),
        mumuText.category(),
        mumuText.showStartDate(),
        mumuText.showEndDate(),
        MumuTextStatus.from(now, mumuText.showStartDate(), mumuText.showEndDate()));
  }
}
