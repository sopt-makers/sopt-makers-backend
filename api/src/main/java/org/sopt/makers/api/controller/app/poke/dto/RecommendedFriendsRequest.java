package org.sopt.makers.api.controller.app.poke.dto;

import java.util.List;
import org.sopt.makers.domain.app.poke.FriendRecommendType;
import org.sopt.makers.domain.app.poke.RecommendedFriends;

public record RecommendedFriendsRequest(List<RecommendedFriendsByType> randomInfoList) {

  public static RecommendedFriendsRequest of(RecommendedFriends recommendedFriends) {
    return new RecommendedFriendsRequest(
        recommendedFriends.randomInfoList().stream().map(RecommendedFriendsByType::of).toList());
  }

  public record RecommendedFriendsByType(
      FriendRecommendType randomType, String randomTitle, List<SimplePokeProfile> userInfoList) {

    private static RecommendedFriendsByType of(
        org.sopt.makers.domain.app.poke.RecommendedFriendsByType data) {
      return new RecommendedFriendsByType(
          data.randomType(),
          data.randomTitle(),
          data.userInfoList().stream().map(SimplePokeProfile::ofNonFriend).toList());
    }
  }
}
