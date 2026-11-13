package org.sopt.makers.api.controller.crew.dining.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "개설자 전용 공동 결제자 대기 요청 목록. 요청 시각 오름차순")
public record GetPayerRequestsResponse(@NotNull List<PayerRequest> requests) {
  public static GetPayerRequestsResponse from(
      org.sopt.makers.domain.crew.dining.result.GetPayerRequestsResult value) {
    return new GetPayerRequestsResponse(value.requests().stream().map(PayerRequest::from).toList());
  }

  @Schema(name = "DiningV2PayerRequestDto", description = "공동 결제자 대기 요청")
  public record PayerRequest(
      @NotNull Long userId,
      @NotNull String name,
      String profileImage,
      @NotNull Long roundId,
      @NotNull int roundNumber,
      @NotNull LocalDateTime requestedAt) {
    public static PayerRequest from(
        org.sopt.makers.domain.crew.dining.result.GetPayerRequestsResult.PayerRequest value) {
      return new PayerRequest(
          value.userId(),
          value.name(),
          value.profileImage(),
          value.roundId(),
          value.roundNumber(),
          value.requestedAt());
    }
  }
}
