package org.sopt.makers.api.controller.crew.dining.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "개설자 전용 승인된 공동 결제자 목록. 회차별로 반환하며 요청 시각 오름차순")
public record GetApprovedPayersResponse(@NotNull List<ApprovedPayer> payers) {
  public static GetApprovedPayersResponse from(
      org.sopt.makers.domain.crew.dining.result.GetApprovedPayersResult value) {
    return new GetApprovedPayersResponse(value.payers().stream().map(ApprovedPayer::from).toList());
  }

  @Schema(name = "DiningV2ApprovedPayerDto", description = "회차별 승인된 공동 결제자")
  public record ApprovedPayer(
      @NotNull Long userId,
      @NotNull String name,
      String profileImage,
      @NotNull Long roundId,
      @NotNull int roundNumber) {
    public static ApprovedPayer from(
        org.sopt.makers.domain.crew.dining.result.GetApprovedPayersResult.ApprovedPayer value) {
      return new ApprovedPayer(
          value.userId(), value.name(), value.profileImage(), value.roundId(), value.roundNumber());
    }
  }
}
