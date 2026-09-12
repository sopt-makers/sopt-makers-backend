package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public final class MissionRequest {

  private MissionRequest() {}

  public record RegisterMissionRequest(
      @Schema(description = "미션 대표 이미지 주소", example = "https://s3.sopt.org/mission/01.png")
          @NotNull(message = "image may not be null")
          String image,
      @Schema(description = "미션 제목", example = "팀원 칭찬하기")
          @NotNull(message = "title may not be null")
          String title,
      @Schema(description = "미션 레벨. 높을수록 점수가 크다", example = "1")
          @NotNull(message = "level may not be null")
          Integer level) {}
}
