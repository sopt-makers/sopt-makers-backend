package org.sopt.makers.domain.playground.coffeechat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoffeeChatSection {

  SOPT_ACTIVITY("SOPT 활동"),
  PLAN("기획"),
  DESIGN("디자인"),
  FRONTEND("프론트"),
  BACKEND("백엔드"),
  APP("앱 개발"),
  ETC("기타");

  private final String title;

  @JsonCreator
  public static CoffeeChatSection fromTitle(String title) {
    return Arrays.stream(CoffeeChatSection.values())
        .filter(s -> s.title.equals(title))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown CoffeeChatSection title: " + title));
  }

  @JsonValue
  public String getTitle() {
    return title;
  }
}
