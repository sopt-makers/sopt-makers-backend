package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamRecentRank;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamRecentRankList;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayTeamRank;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTodayTeamRankList;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;

public final class AppjamRankResponse {

  private AppjamRankResponse() {}

  public record AppjamtampRankResponse(
      @Schema(description = "스탬프 아이디", example = "1") Long stampId,
      @Schema(description = "인증한 미션 아이디", example = "1") Long missionId,
      @Schema(description = "인증한 유저 아이디", example = "1") Long userId,
      @Schema(description = "인증 이미지 주소", example = "https://s3.sopt.org/stamp/uuid.png")
          String imageUrl,
      @Schema(description = "인증 일시", example = "2026-09-19T14:00:00") LocalDateTime createdAt,
      @Schema(description = "인증한 유저의 솝탬프 닉네임", example = "김앱짱") String ownerNickname,
      @Schema(description = "인증한 유저 이름", example = "김앱짱") String userName,
      @Schema(description = "인증한 유저 프로필 이미지 주소", example = "https://s3.sopt.org/profile.png")
          String userProfileImage,
      @Schema(description = "소속 팀 이름", example = "1팀") String teamName,
      @Schema(description = "소속 팀 번호") TeamNumber teamNumber) {

    public static AppjamtampRankResponse of(AppjamRecentRank rank) {
      return new AppjamtampRankResponse(
          rank.stampId(),
          rank.missionId(),
          rank.userId(),
          rank.imageUrl(),
          rank.createdAt(),
          rank.ownerNickname(),
          rank.userName(),
          rank.userProfileImage(),
          rank.teamName(),
          rank.teamNumber());
    }
  }

  public record AppjamtampRankListResponse(
      @Schema(description = "최근 인증한 미션 목록. 최신순") List<AppjamtampRankResponse> ranks) {

    public static AppjamtampRankListResponse of(AppjamRecentRankList rankList) {
      return new AppjamtampRankListResponse(
          rankList.ranks().stream().map(AppjamtampRankResponse::of).toList());
    }
  }

  public record AppjamTodayTeamRankResponse(
      @Schema(description = "순위. 1부터 시작", example = "1") int rank,
      @Schema(description = "팀 이름", example = "1팀") String teamName,
      @Schema(description = "팀 번호") TeamNumber teamNumber,
      @Schema(description = "오늘 얻은 점수", example = "30") long todayPoints,
      @Schema(description = "누적 점수", example = "120") long totalPoints) {

    public static AppjamTodayTeamRankResponse of(AppjamTodayTeamRank rank) {
      return new AppjamTodayTeamRankResponse(
          rank.rank(), rank.teamName(), rank.teamNumber(), rank.todayPoints(), rank.totalPoints());
    }
  }

  public record AppjamTodayRankListResponse(
      @Schema(description = "오늘의 팀 랭킹 목록") List<AppjamTodayTeamRankResponse> ranks) {

    public static AppjamTodayRankListResponse of(AppjamTodayTeamRankList rankList) {
      return new AppjamTodayRankListResponse(
          rankList.ranks().stream().map(AppjamTodayTeamRankResponse::of).toList());
    }
  }
}
