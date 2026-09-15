package org.sopt.makers.api.controller.app.poke.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PokeMessageRequest(
    @Schema(description = "보낼 찌르기 메시지", example = "안녕 친구야") String message,
    @Schema(description = "익명으로 보낼지 여부. 주지 않으면 false", example = "false") Boolean isAnonymous) {

  public PokeMessageRequest {
    isAnonymous = Boolean.TRUE.equals(isAnonymous);
  }
}
