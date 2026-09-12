package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.soptamp.SoptampUser;

public record SoptampUserResponse(
    @Schema(description = "솝탬프 닉네임", example = "김앱짱") String nickname,
    @Schema(description = "누적 점수", example = "120") Long points,
    @Schema(description = "프로필 한마디", example = "안녕하세요") String profileMessage) {

  public static SoptampUserResponse of(SoptampUser soptampUser) {
    return new SoptampUserResponse(
        soptampUser.nickname(), soptampUser.totalPoints(), soptampUser.profileMessage());
  }
}
