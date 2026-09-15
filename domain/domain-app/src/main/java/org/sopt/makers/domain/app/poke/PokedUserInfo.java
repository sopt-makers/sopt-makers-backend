package org.sopt.makers.domain.app.poke;

import java.util.List;

public record PokedUserInfo(
    Long userId,
    String name,
    String profileImage,
    Long generation,
    String part,
    Relationship relation,
    List<String> mutualFriendNames) {

  private static final String NEW_FRIEND_NO_MUTUAL = "새로운 친구";
  private static final String NEW_FRIEND_ONE_MUTUAL = "%s의 친구";
  private static final String NEW_FRIEND_MANY_MUTUAL = "%s 외 %d명과 친구";

  public String getMutualRelationMessage() {
    int size = mutualFriendNames.size();
    if (size == 0) {
      return NEW_FRIEND_NO_MUTUAL;
    }
    if (size == 1) {
      return String.format(NEW_FRIEND_ONE_MUTUAL, mutualFriendNames.getFirst());
    }
    return String.format(NEW_FRIEND_MANY_MUTUAL, mutualFriendNames.getFirst(), size - 1);
  }

  public boolean isFirstMeet() {
    return relation.pokeNum() < 2;
  }
}
