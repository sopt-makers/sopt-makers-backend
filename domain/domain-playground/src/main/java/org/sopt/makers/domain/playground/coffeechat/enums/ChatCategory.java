package org.sopt.makers.domain.playground.coffeechat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatCategory {

  COFFEE_CHAT("커피챗"),
  FRIENDSHIP("친목"),
  APPJAM_TEAM_BUILD("앱잼 팀 빌딩"),
  PROJECT_PROPOSAL("프로젝트 제안"),
  OTHER("기타");

  private final String title;

  @JsonCreator
  public static ChatCategory fromTitle(String title) {
    return Arrays.stream(ChatCategory.values())
        .filter(c -> c.title.equals(title))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown ChatCategory title: " + title));
  }

  @JsonValue
  public String getTitle() {
    return title;
  }
}
