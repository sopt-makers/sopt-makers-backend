package org.sopt.makers.api.controller.playground.wordchaingame;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum WordChainGameSuccessCode implements SuccessCode {

  CREATE_WORD(200, "단어 전송에 성공했습니다."),
  GET_GAME_ROOMS(200, "게임 전체 조회에 성공했습니다."),
  CREATE_GAME_ROOM(200, "새 게임 생성에 성공했습니다."),
  GET_WINNERS(200, "명예의 전당 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
