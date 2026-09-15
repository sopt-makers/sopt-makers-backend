package org.sopt.makers.domain.app.soptamp.appjam;

import java.util.List;

public record AppjamTodayTeamRankList(List<AppjamTodayTeamRank> ranks) {

  public static AppjamTodayTeamRankList of(List<AppjamTodayTeamRank> ranks) {
    return new AppjamTodayTeamRankList(ranks);
  }
}
