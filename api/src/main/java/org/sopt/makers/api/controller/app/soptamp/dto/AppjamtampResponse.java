package org.sopt.makers.api.controller.app.soptamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
      @Schema(description = "미션 아이디", example = "1") Long id,
      @Schema(description = "미션 제목", example = "팀원 칭찬하기") String title,
      @Schema(description = "미션을 인증한 팀원 닉네임. 아직 아무도 안 했으면 null", example = "김앱짱") String ownerName,
      @Schema(description = "미션 레벨", example = "1") Integer level,
      @Schema(description = "미션 이미지 주소 목록. 이미지가 없으면 빈 배열") List<String> profileImage,
      @Schema(description = "팀이 이 미션을 인증했는지 여부", example = "false") @JsonProperty("isCompleted")
          Boolean isCompleted) {

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
      @Schema(description = "내 팀 번호. 앱잼에 참여하지 않으면 null") TeamNumber myTeamNumber,
      @Schema(description = "내가 앱잼에 참여 중인지 여부", example = "true") @JsonProperty("isAppjamJoined")
          boolean isAppjamJoined,
      @Schema(description = "조회한 팀 번호") TeamNumber teamNumber,
      @Schema(description = "조회한 팀 이름", example = "1팀") String teamName,
      @Schema(description = "팀의 미션 목록") List<AppjamMissionResponse> missions) {

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
      @Schema(description = "스탬프 아이디", example = "1") Long id,
      @Schema(description = "인증 내용", example = "팀원들과 함께 했어요") String contents,
      @Schema(description = "인증 이미지 주소 목록") List<String> images,
      @Schema(description = "활동 날짜", example = "2026-09-19") String activityDate,
      @Schema(description = "등록 일시", example = "2026-09-19T14:00:00") LocalDateTime createdAt,
      @Schema(description = "마지막 수정 일시", example = "2026-09-19T15:00:00") LocalDateTime updatedAt,
      @Schema(description = "인증한 미션 아이디", example = "1") Long missionId,
      @Schema(description = "인증한 미션 제목", example = "팀원 칭찬하기") String missionTitle,
      @Schema(description = "인증한 미션 레벨", example = "1") Integer missionLevel,
      @Schema(description = "작성자의 팀 번호") TeamNumber teamNumber,
      @Schema(description = "작성자의 팀 이름", example = "1팀") String teamName,
      @Schema(description = "작성자의 솝탬프 닉네임", example = "김앱짱") String ownerNickname,
      @Schema(description = "작성자 프로필 이미지 주소", example = "https://s3.sopt.org/profile.png")
          String ownerProfileImage,
      @Schema(description = "받은 박수 수", example = "42") int clapCount,
      @Schema(description = "조회 수", example = "120") int viewCount,
      @Schema(description = "내가 쓴 스탬프인지 여부", example = "true") @JsonProperty("isMine")
          boolean isMine,
      @Schema(description = "내가 이 스탬프에 보낸 박수 수", example = "5") int myClapCount) {

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
      @Schema(description = "스탬프 아이디", example = "1") Long id,
      @Schema(description = "인증 내용", example = "팀원들과 함께 했어요") String contents,
      @Schema(description = "인증 이미지 주소 목록") List<String> images,
      @Schema(description = "활동 날짜", example = "2026-09-19") String activityDate,
      @Schema(description = "등록 일시", example = "2026-09-19T14:00:00") LocalDateTime createdAt,
      @Schema(description = "마지막 수정 일시", example = "2026-09-19T15:00:00") LocalDateTime updatedAt,
      @Schema(description = "작성자의 솝탬프 닉네임", example = "김앱짱") String ownerNickname,
      @Schema(description = "작성자 프로필 이미지 주소", example = "https://s3.sopt.org/profile.png")
          String ownerProfileImage,
      @Schema(description = "인증한 미션 아이디", example = "1") Long missionId,
      @Schema(description = "받은 박수 수", example = "42") int clapCount,
      @Schema(description = "조회 수", example = "120") int viewCount) {

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
