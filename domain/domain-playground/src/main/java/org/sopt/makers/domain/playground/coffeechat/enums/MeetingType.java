package org.sopt.makers.domain.playground.coffeechat.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingType {

  ONLINE("온라인"),
  OFFLINE("오프라인"),
  ANYTHING("온/오프라인");

  private final String title;

  @JsonCreator
  public static MeetingType fromTitle(String title) {
    return Arrays.stream(MeetingType.values())
        .filter(t -> t.title.equals(title))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown MeetingType title: " + title));
  }

  @JsonValue
  public String getTitle() {
    return title;
  }
}
