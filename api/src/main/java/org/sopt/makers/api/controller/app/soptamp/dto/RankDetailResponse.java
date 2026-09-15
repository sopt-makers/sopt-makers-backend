package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade.UserMissions;

public record RankDetailResponse(
    @Schema(description = "솝탬프 닉네임", example = "김앱짱") String nickname,
    @Schema(description = "프로필 한마디", example = "안녕하세요") String profileMessage,
    @Schema(description = "유저가 인증한 미션 목록") List<RankMissionResponse> userMissions) {

  public static RankDetailResponse of(UserMissions userMissions) {
    return new RankDetailResponse(
        userMissions.user().nickname(),
        userMissions.user().profileMessage(),
        userMissions.missions().stream().map(RankMissionResponse::of).toList());
  }
}
