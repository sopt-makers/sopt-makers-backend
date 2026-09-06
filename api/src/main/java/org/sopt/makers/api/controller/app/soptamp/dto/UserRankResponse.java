package org.sopt.makers.api.controller.app.soptamp.dto;

import org.sopt.makers.domain.app.soptamp.rank.UserRank;

public record UserRankResponse(Integer rank, String nickname, Long point, String profileMessage) {

  public static UserRankResponse of(UserRank userRank) {
    return new UserRankResponse(
        userRank.rank(), userRank.nickname(), userRank.point(), userRank.profileMessage());
  }
}
