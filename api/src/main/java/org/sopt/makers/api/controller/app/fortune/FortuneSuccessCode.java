package org.sopt.makers.api.controller.app.fortune;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum FortuneSuccessCode implements SuccessCode {
  GET_TODAY_FORTUNE_WORD(200, "오늘의 솝마디 조회에 성공했습니다."),
  GET_TODAY_FORTUNE_CARD(200, "오늘의 운세카드 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
