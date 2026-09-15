package org.sopt.makers.api.controller.app.soptamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.mission.MissionCompleteness;

public final class MissionResponse {

  private MissionResponse() {}

  public record MissionMain(
      @Schema(description = "미션 아이디", example = "1") Long id,
      @Schema(description = "미션 제목", example = "팀원 칭찬하기") String title,
      @Schema(description = "미션 레벨", example = "1") Integer level,
      @Schema(description = "미션 이미지 주소 목록. 이미지가 없으면 빈 배열") List<String> profileImage) {

    public static MissionMain of(Mission mission) {
      return new MissionMain(
          mission.id(), mission.title(), mission.level(), mission.profileImages());
    }
  }

  public record Completeness(
      @Schema(description = "미션 아이디", example = "1") Long id,
      @Schema(description = "미션 제목", example = "팀원 칭찬하기") String title,
      @Schema(description = "미션 레벨", example = "1") Integer level,
      @Schema(description = "미션 이미지 주소 목록. 이미지가 없으면 빈 배열") List<String> profileImage,
      @Schema(description = "내가 이 미션을 인증했는지 여부", example = "false") @JsonProperty("isCompleted")
          Boolean isCompleted) {

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

  public record MissionId(@Schema(description = "미션 아이디", example = "1") Long missionId) {

    public static MissionId of(Mission mission) {
      return new MissionId(mission.id());
    }
  }
}
