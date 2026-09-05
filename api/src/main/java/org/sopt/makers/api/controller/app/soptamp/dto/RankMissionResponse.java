package org.sopt.makers.api.controller.app.soptamp.dto;

import java.util.List;
import org.sopt.makers.domain.app.soptamp.mission.Mission;

public record RankMissionResponse(
    Long id, String title, Integer level, Boolean display, List<String> profileImage) {

  public static RankMissionResponse of(Mission mission) {
    return new RankMissionResponse(
        mission.id(), mission.title(), mission.level(), mission.display(), mission.profileImages());
  }
}
