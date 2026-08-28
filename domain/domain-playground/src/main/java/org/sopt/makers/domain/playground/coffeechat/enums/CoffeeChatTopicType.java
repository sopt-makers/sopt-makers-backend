package org.sopt.makers.domain.playground.coffeechat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoffeeChatTopicType {

  STARTUP("창업"),
  CAREER("커리어"),
  PORTFOLIO("포트폴리오"),
  RESUME("이력서/자소서"),
  MEETING("면접"),
  PROFESSIONALISM("직무 전문성"),
  IMPROVEMENT("자기계발"),
  NETWORKING("네트워킹"),
  PROJECT("프로젝트"),
  ETC("기타");

  private final String title;

  @JsonCreator
  public static CoffeeChatTopicType fromTitle(String title) {
    return Arrays.stream(CoffeeChatTopicType.values())
        .filter(t -> t.title.equals(title))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown CoffeeChatTopicType title: " + title));
  }

  @JsonValue
  public String getTitle() {
    return title;
  }
}
