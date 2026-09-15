package org.sopt.makers.domain.app.soptamp.appjam;

import java.util.List;
import org.sopt.makers.domain.app.soptamp.mission.Mission;

public record AppjamMission(
    Long id,
    String title,
    String ownerName,
    Integer level,
    List<String> profileImage,
    boolean isCompleted) {

  public static AppjamMission of(Mission mission, boolean completed, String ownerName) {
    return new AppjamMission(
        mission.id(),
        mission.title(),
        ownerName,
        mission.level(),
        mission.profileImages(),
        completed);
  }

  public static AppjamMission createWhenUncompleted(Mission mission) {
    return new AppjamMission(
        mission.id(), mission.title(), null, mission.level(), mission.profileImages(), false);
  }
}
