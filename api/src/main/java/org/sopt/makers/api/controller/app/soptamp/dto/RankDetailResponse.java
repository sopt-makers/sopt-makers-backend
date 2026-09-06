package org.sopt.makers.api.controller.app.soptamp.dto;

import java.util.List;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade.UserMissions;

public record RankDetailResponse(
    String nickname, String profileMessage, List<RankMissionResponse> userMissions) {

  public static RankDetailResponse of(UserMissions userMissions) {
    return new RankDetailResponse(
        userMissions.user().nickname(),
        userMissions.user().profileMessage(),
        userMissions.missions().stream().map(RankMissionResponse::of).toList());
  }
}
