package org.sopt.makers.api.controller.app.poke.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.poke.SimplePokeProfileData;

public record AllRelationFriendList(
    @Schema(description = "친한친구 구간(2~4회) 목록. 최대 2명") List<SimplePokeProfile> newFriend,
    @Schema(description = "친한친구 구간 전체 수", example = "5") int newFriendSize,
    @Schema(description = "단짝친구 구간(5~10회) 목록. 최대 2명") List<SimplePokeProfile> bestFriend,
    @Schema(description = "단짝친구 구간 전체 수", example = "3") int bestFriendSize,
    @Schema(description = "천생연분 구간(11회 이상) 목록. 최대 2명") List<SimplePokeProfile> soulmate,
    @Schema(description = "천생연분 구간 전체 수", example = "1") int soulmateSize,
    @Schema(description = "전체 친구 수", example = "9") int totalSize) {

  public static AllRelationFriendList of(
      List<SimplePokeProfileData> newFriend,
      int newFriendSize,
      List<SimplePokeProfileData> bestFriend,
      int bestFriendSize,
      List<SimplePokeProfileData> soulmate,
      int soulmateSize) {
    return new AllRelationFriendList(
        toProfiles(newFriend),
        newFriendSize,
        toProfiles(bestFriend),
        bestFriendSize,
        toProfiles(soulmate),
        soulmateSize,
        newFriendSize + bestFriendSize + soulmateSize);
  }

  private static List<SimplePokeProfile> toProfiles(List<SimplePokeProfileData> data) {
    return data.stream().map(SimplePokeProfile::of).toList();
  }
}
