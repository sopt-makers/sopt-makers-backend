package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.soptamp.mission.Mission;

public record RankMissionResponse(
    @Schema(description = "미션 아이디", example = "1") Long id,
    @Schema(description = "미션 제목", example = "팀원 칭찬하기") String title,
    @Schema(description = "미션 레벨", example = "1") Integer level,
    @Schema(description = "목록에 띄울지 여부", example = "true") Boolean display,
    @Schema(description = "미션 이미지 주소 목록. 이미지가 없으면 빈 배열") List<String> profileImage) {

  public static RankMissionResponse of(Mission mission) {
    return new RankMissionResponse(
        mission.id(), mission.title(), mission.level(), mission.display(), mission.profileImages());
  }
}
