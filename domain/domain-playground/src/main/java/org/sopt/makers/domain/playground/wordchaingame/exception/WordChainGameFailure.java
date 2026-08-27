package org.sopt.makers.domain.playground.wordchaingame.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum WordChainGameFailure implements FailureCode {

  NOT_FOUND_ROOM(400, "없는 방 번호입니다."),
  NOT_VALID_WORD(400, "한글자 단어는 사용할 수 없어요."),
  NOT_KOREAN_WORD(400, "한글 이외의 문자는 허용되지 않아요."),
  WORD_NOT_IN_DICTIONARY(400, "표준국어대사전에 존재하지 않는 단어예요."),
  DUPLICATE_WORD(400, "이미 누군가 사용한 단어예요."),
  NOT_CHAINING_WORD(400, "끝말을 잇는 단어가 아니에요."),
  CANNOT_USE_LAST_WRITERS_WORD(400, "본인 단어에는 단어를 이을 수 없어요."),
  CANNOT_CREATE_GAME_AS_LAST_WRITER(400, "마지막 단어 작성자는 새로 게임을 시작할 수 없어요."),
  NO_WORD_IN_ROOM(400, "이전 게임에 아무도 답을 하지 않은 경우에는 새로운 방을 만들 수 없어요.");

  private final int statusCode;
  private final String message;
}
