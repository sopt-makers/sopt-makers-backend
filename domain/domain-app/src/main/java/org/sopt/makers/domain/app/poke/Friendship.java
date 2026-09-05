package org.sopt.makers.domain.app.poke;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.exception.PokeFailure;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Friendship {
  NON_FRIEND("nonfriend", "새로운 친구", 0, 2),
  NEW_FRIEND("new", "친한친구", 2, 5),
  BEST_FRIEND("bestfriend", "단짝친구", 5, 11),
  SOULMATE("soulmate", "천생연분", 11, Integer.MAX_VALUE);

  private final String typeFlag;
  private final String friendshipName;
  private final int lowerLimit;
  private final int upperLimit;

  public static Friendship getFriendshipByValue(String type) {
    return Arrays.stream(values())
        .filter(friendship -> friendship.typeFlag.equals(type))
        .findFirst()
        .orElseThrow(() -> new PokeException(PokeFailure.NOT_FOUND_FRIENDSHIP));
  }

  public static String decideRelationName(int pokeCount) {
    if (pokeCount >= SOULMATE.lowerLimit) {
      return SOULMATE.friendshipName;
    }
    if (pokeCount >= BEST_FRIEND.lowerLimit) {
      return BEST_FRIEND.friendshipName;
    }
    if (pokeCount >= NEW_FRIEND.lowerLimit) {
      return NEW_FRIEND.friendshipName;
    }
    return NON_FRIEND.friendshipName;
  }
}
