package org.sopt.makers.domain.app.poke;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.exception.PokeFailure;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PokeMessageType {

  // 친구가 아닌 사람을 내가 먼저 찌를 때 필요한 찌르기 메시지
  POKE_SOMEONE("pokeSomeone"),

  // 친구와 찔렀을 때 & 친구에게 찔렸을 때 필요한 메시지
  POKE_FRIEND("pokeFriend"),

  // 친구가 아닌 사람이 나를 찔렀을 때 필요한 답찌르기 메시지
  REPLY_NEW("replyNew"),

  // 모든 상황에서 사용하는 메시지
  POKE_ALL("pokeAll");

  private final String parameter;

  public static PokeMessageType ofParam(String parameter) {
    return Arrays.stream(values())
        .filter(value -> value.parameter.equals(parameter))
        .findFirst()
        .orElseThrow(() -> new PokeException(PokeFailure.NOT_FOUND_POKE_MESSAGE_TYPE));
  }

  public String getParameter() {
    return parameter;
  }
}
