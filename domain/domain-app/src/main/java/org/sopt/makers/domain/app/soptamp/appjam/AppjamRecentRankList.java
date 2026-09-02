package org.sopt.makers.domain.app.soptamp.appjam;

import java.util.List;

public record AppjamRecentRankList(List<AppjamRecentRank> ranks) {

  public static AppjamRecentRankList of(List<AppjamRecentRank> ranks) {
    return new AppjamRecentRankList(ranks);
  }
}
