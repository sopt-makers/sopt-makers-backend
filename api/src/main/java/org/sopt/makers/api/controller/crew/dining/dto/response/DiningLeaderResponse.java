package org.sopt.makers.api.controller.crew.dining.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "DiningLeaderResponse", description = "회식 개설자")
public record DiningLeaderResponse(
    @Schema(description = "개설자 크루 사용자 id", example = "1") @NotNull Long id,
    @Schema(description = "개설자 이름", example = "홍길동") @NotNull String name,
    @Schema(description = "개설자 프로필 이미지", example = "[url] 형식") String profileImage) {
  public static DiningLeaderResponse from(
      org.sopt.makers.domain.crew.dining.result.DiningLeader value) {
    return new DiningLeaderResponse(value.id(), value.name(), value.profileImage());
  }
}
