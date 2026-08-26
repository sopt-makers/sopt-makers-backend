package org.sopt.makers.api.controller.app.poke.dto;

import java.util.List;
import org.sopt.makers.domain.app.poke.SimplePokeProfileData;

public record AllRelationFriendList(
    List<SimplePokeProfile> newFriend,
    int newFriendSize,
    List<SimplePokeProfile> bestFriend,
    int bestFriendSize,
    List<SimplePokeProfile> soulmate,
    int soulmateSize,
    int totalSize) {

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
