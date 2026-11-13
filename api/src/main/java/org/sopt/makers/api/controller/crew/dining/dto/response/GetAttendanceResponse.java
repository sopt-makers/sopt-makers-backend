package org.sopt.makers.api.controller.crew.dining.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.sopt.makers.domain.crew.dining.model.enums.DiningPayerStatus;

@Schema(description = "회차 참석 현황")
public record GetAttendanceResponse(
    @NotNull long attendeeCount,
    @NotNull boolean isAttending,
    @Schema(description = "본인의 해당 회차 공동 결제자 상태. 미참석 또는 요청 없음이면 null")
        DiningPayerStatus payerStatus) {
  public static GetAttendanceResponse from(
      org.sopt.makers.domain.crew.dining.result.GetAttendanceResult value) {
    return new GetAttendanceResponse(
        value.attendeeCount(), value.isAttending(), value.payerStatus());
  }
}
