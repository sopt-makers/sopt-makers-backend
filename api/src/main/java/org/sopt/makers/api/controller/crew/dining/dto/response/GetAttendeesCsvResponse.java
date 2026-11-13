package org.sopt.makers.api.controller.crew.dining.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회차 전체 참석자 CSV 다운로드 URL")
public record GetAttendeesCsvResponse(
    @Schema(description = "S3에 업로드된 CSV 파일 URL") @NotNull String url) {
  public static GetAttendeesCsvResponse from(
      org.sopt.makers.domain.crew.dining.result.GetAttendeesCsvResult value) {
    return new GetAttendeesCsvResponse(value.url());
  }
}
