package org.sopt.makers.api.controller.app.soptamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.mission.MissionCompleteness;

public final class MissionResponse {

  private MissionResponse() {}

  public record MissionMain(Long id, String title, Integer level, List<String> profileImage) {

    public static MissionMain of(Mission mission) {
      return new MissionMain(
          mission.id(), mission.title(), mission.level(), mission.profileImages());
    }
  }

  public record Completeness(
      Long id,
      String title,
      Integer level,
      List<String> profileImage,
      @JsonProperty("isCompleted") Boolean isCompleted) {

    public static Completeness of(MissionCompleteness completeness) {
      Mission mission = completeness.mission();
      return new Completeness(
          mission.id(),
          mission.title(),
          mission.level(),
          mission.profileImages(),
          completeness.completed());
    }
  }

  public record MissionId(Long missionId) {

    public static MissionId of(Mission mission) {
      return new MissionId(mission.id());
    }
  }
}
