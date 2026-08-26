package org.sopt.makers.domain.app.poke.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PokeFailure implements FailureCode {
  NOT_FOUND_USER(404, "존재하지 않는 유저입니다."),
  NOT_FOUND_POKE_HISTORY(404, "해당 찌르기 내역은 존재하지 않습니다."),
  NOT_FOUND_POKE_MESSAGE_TYPE(404, "해당 찌르기 메시지 타입은 존재하지 않습니다."),
  NOT_FOUND_FRIENDSHIP(404, "해당 친구관계는 존재하지 않습니다."),
  SELF_POKE_NOT_ALLOWED(400, "본인을 찌를 수 없습니다."),
  DUPLICATE_POKE(409, "이미 찌르기를 보낸 친구입니다.");

  private final int statusCode;
  private final String message;
}
