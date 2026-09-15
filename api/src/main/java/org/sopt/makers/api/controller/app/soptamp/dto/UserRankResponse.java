package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.soptamp.rank.UserRank;

public record UserRankResponse(
    @Schema(description = "순위. 1부터 시작", example = "1") Integer rank,
    @Schema(description = "솝탬프 닉네임", example = "김앱짱") String nickname,
    @Schema(description = "누적 점수", example = "120") Long point,
    @Schema(description = "프로필 한마디", example = "안녕하세요") String profileMessage) {

  public static UserRankResponse of(UserRank userRank) {
    return new UserRankResponse(
        userRank.rank(), userRank.nickname(), userRank.point(), userRank.profileMessage());
  }
}
