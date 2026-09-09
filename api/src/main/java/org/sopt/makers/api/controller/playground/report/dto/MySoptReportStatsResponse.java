package org.sopt.makers.api.controller.playground.report.dto;

import java.util.List;
import org.sopt.makers.domain.playground.report.service.SoptReportStatsQueryService.MySoptReportStatsResult;

public record MySoptReportStatsResponse(
    String myType,
    Long totalVisitCount,
    CommunityStatsDto myCommunityStats,
    ProfileStatsDto myProfileStats,
    CrewStatsDto myCrewStats,
    WordChainGameStatsDto myWordChainGameStats) {

  public static MySoptReportStatsResponse from(MySoptReportStatsResult result) {
    return new MySoptReportStatsResponse(
        result.myType(),
        result.totalVisitCount(),
        new CommunityStatsDto(result.likeCount()),
        new ProfileStatsDto(result.viewCount()),
        new CrewStatsDto(result.topFastestJoinedGroupList()),
        new WordChainGameStatsDto(result.playCount(), result.winCount(), result.wordList()));
  }

  public record CommunityStatsDto(Integer likeCount) {}

  public record ProfileStatsDto(Long viewCount) {}

  public record CrewStatsDto(List<String> topFastestJoinedGroupList) {}

  public record WordChainGameStatsDto(Integer playCount, Integer winCount, List<String> wordList) {}
}
