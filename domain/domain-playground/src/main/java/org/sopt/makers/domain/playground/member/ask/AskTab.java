package org.sopt.makers.domain.playground.member.ask;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AskTab {
  ANSWERED("answered", "답변 완료"),
  UNANSWERED("unanswered", "새질문");

  private final String value;
  private final String description;

  /** 레거시 호환: 값이 없거나 인식할 수 없는 문자열이면 ANSWERED로 대체한다. */
  public static AskTab from(String value) {
    if (value == null) {
      return ANSWERED;
    }
    for (AskTab tab : values()) {
      if (tab.value.equalsIgnoreCase(value)) {
        return tab;
      }
    }
    return ANSWERED;
  }
}
