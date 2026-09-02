package org.sopt.makers.api.controller.app.soptamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamMission;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamMissionList;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.facade.AppjamtampFacade;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;

public final class AppjamtampResponse {

  private AppjamtampResponse() {}

  public record AppjamMissionResponse(
      Long id,
      String title,
      String ownerName,
      Integer level,
      List<String> profileImage,
      @JsonProperty("isCompleted") Boolean isCompleted) {

    public static AppjamMissionResponse of(AppjamMission mission) {
      return new AppjamMissionResponse(
          mission.id(),
          mission.title(),
          mission.ownerName(),
          mission.level(),
          mission.profileImage(),
          mission.isCompleted());
    }
  }

  public record AppjamMissionResponses(
      TeamNumber myTeamNumber,
      @JsonProperty("isAppjamJoined") boolean isAppjamJoined,
      TeamNumber teamNumber,
      String teamName,
      List<AppjamMissionResponse> missions) {

    public static AppjamMissionResponses of(AppjamMissionList missionList) {
      return new AppjamMissionResponses(
          missionList.myTeamNumber(),
          missionList.isAppjamJoined(),
          missionList.teamNumber(),
          missionList.teamName(),
          missionList.missions().stream().map(AppjamMissionResponse::of).toList());
    }
  }

  public record AppjamtampView(
      Long id,
      String contents,
      List<String> images,
      String activityDate,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      Long missionId,
      String missionTitle,
      Integer missionLevel,
      TeamNumber teamNumber,
      String teamName,
      String ownerNickname,
      String ownerProfileImage,
      int clapCount,
      int viewCount,
      @JsonProperty("isMine") boolean isMine,
      int myClapCount) {

    public static AppjamtampView of(AppjamtampFacade.AppjamtampView view) {
      Stamp stamp = view.stamp();
      return new AppjamtampView(
          stamp.id(),
          stamp.contents(),
          stamp.images(),
          stamp.activityDate(),
          stamp.createdAt(),
          stamp.updatedAt(),
          stamp.missionId(),
          view.mission().title(),
          view.mission().level(),
          view.teamSummary().teamNumber(),
          view.teamSummary().teamName(),
          view.ownerNickname(),
          view.ownerProfileImage(),
          stamp.clapCount(),
          stamp.viewCount(),
          view.mine(),
          view.myClapCount());
    }
  }

  public record StampMain(
      Long id,
      String contents,
      List<String> images,
      String activityDate,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String ownerNickname,
      String ownerProfileImage,
      Long missionId,
      int clapCount,
      int viewCount) {

    public static StampMain of(AppjamtampFacade.StampWithProfile stampWithProfile) {
      Stamp stamp = stampWithProfile.stamp();
      return new StampMain(
          stamp.id(),
          stamp.contents(),
          stamp.images(),
          stamp.activityDate(),
          stamp.createdAt(),
          stamp.updatedAt(),
          stampWithProfile.ownerNickname(),
          stampWithProfile.ownerProfileImage(),
          stamp.missionId(),
          stamp.clapCount(),
          stamp.viewCount());
    }
  }
}
