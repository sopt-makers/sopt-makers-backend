package org.sopt.makers.api.controller.crew.dining.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CreateRoundResponse", description = "회차 추가 응답 Dto")
public record CreateRoundResponse(
    @Schema(description = "회차 id", example = "2") @NotNull Long roundId) {
  public static CreateRoundResponse from(
      org.sopt.makers.domain.crew.dining.result.CreateRoundResult value) {
    return new CreateRoundResponse(value.roundId());
  }
}
