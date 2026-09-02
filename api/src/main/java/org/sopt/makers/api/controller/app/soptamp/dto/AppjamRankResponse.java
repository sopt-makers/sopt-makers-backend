package org.sopt.makers.api.controller.app.soptamp.dto;

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
      Long stampId,
      Long missionId,
      Long userId,
      String imageUrl,
      LocalDateTime createdAt,
      String ownerNickname,
      String userName,
      String userProfileImage,
      String teamName,
      TeamNumber teamNumber) {

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

  public record AppjamtampRankListResponse(List<AppjamtampRankResponse> ranks) {

    public static AppjamtampRankListResponse of(AppjamRecentRankList rankList) {
      return new AppjamtampRankListResponse(
          rankList.ranks().stream().map(AppjamtampRankResponse::of).toList());
    }
  }

  public record AppjamTodayTeamRankResponse(
      int rank, String teamName, TeamNumber teamNumber, long todayPoints, long totalPoints) {

    public static AppjamTodayTeamRankResponse of(AppjamTodayTeamRank rank) {
      return new AppjamTodayTeamRankResponse(
          rank.rank(), rank.teamName(), rank.teamNumber(), rank.todayPoints(), rank.totalPoints());
    }
  }

  public record AppjamTodayRankListResponse(List<AppjamTodayTeamRankResponse> ranks) {

    public static AppjamTodayRankListResponse of(AppjamTodayTeamRankList rankList) {
      return new AppjamTodayRankListResponse(
          rankList.ranks().stream().map(AppjamTodayTeamRankResponse::of).toList());
    }
  }
}
