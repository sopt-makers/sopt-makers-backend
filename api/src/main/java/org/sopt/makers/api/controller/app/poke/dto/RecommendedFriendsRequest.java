package org.sopt.makers.api.controller.app.poke.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.poke.FriendRecommendType;
import org.sopt.makers.domain.app.poke.RecommendedFriends;

public record RecommendedFriendsRequest(
    @Schema(description = "추천 분류별 친구 후보 목록") List<RecommendedFriendsByType> randomInfoList) {

  public static RecommendedFriendsRequest of(RecommendedFriends recommendedFriends) {
    return new RecommendedFriendsRequest(
        recommendedFriends.randomInfoList().stream().map(RecommendedFriendsByType::of).toList());
  }

  public record RecommendedFriendsByType(
      @Schema(description = "추천 분류") FriendRecommendType randomType,
      @Schema(description = "추천 분류 제목", example = "나와 MBTI가 같은 사람") String randomTitle,
      @Schema(description = "추천된 유저 목록. 아직 친구가 아니라 관계 관련 값은 비어 있다")
          List<SimplePokeProfile> userInfoList) {

    private static RecommendedFriendsByType of(
        org.sopt.makers.domain.app.poke.RecommendedFriendsByType data) {
      return new RecommendedFriendsByType(
          data.randomType(),
          data.randomTitle(),
          data.userInfoList().stream().map(SimplePokeProfile::ofNonFriend).toList());
    }
  }
}
