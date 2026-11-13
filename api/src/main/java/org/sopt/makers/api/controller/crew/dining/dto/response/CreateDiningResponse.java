package org.sopt.makers.api.controller.crew.dining.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CreateDiningResponse", description = "회식 등록 응답 Dto")
public record CreateDiningResponse(
    @Schema(description = "회식(모임) id", example = "1") @NotNull Long meetingId) {
  public static CreateDiningResponse from(
      org.sopt.makers.domain.crew.dining.result.CreateDiningResult value) {
    return new CreateDiningResponse(value.meetingId());
  }
}
