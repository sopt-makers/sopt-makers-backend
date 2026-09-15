package org.sopt.makers.api.controller.app.poke;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum PokeSuccessCode implements SuccessCode {
  GET_IS_NEW_USER(200, "신규 유저 여부 조회에 성공했습니다."),
  GET_POKE_MESSAGES(200, "찌르기 메시지 조회에 성공했습니다."),
  POKE_FRIEND(200, "찌르기에 성공했습니다."),
  GET_FRIEND(200, "찔러볼 친구 조회에 성공했습니다."),
  GET_RANDOM_UNREPLIED_POKE_ME(200, "나를 찌른 친구 단일 조회에 성공했습니다."),
  GET_ALL_POKE_ME(200, "나를 찌른 친구 목록 조회에 성공했습니다."),
  GET_FRIEND_LIST(200, "친구 목록 조회에 성공했습니다."),
  GET_RECOMMENDED_FRIENDS(200, "친구 추천 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
