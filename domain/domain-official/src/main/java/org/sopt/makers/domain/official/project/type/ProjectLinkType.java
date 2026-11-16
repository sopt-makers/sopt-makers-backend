package org.sopt.makers.domain.official.project.type;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectLinkType {
  WEBSITE("website"),
  GOOGLE_PLAYSTORE("googlePlay"),
  APP_STORE("appStore"),
  GITHUB("github"),
  MEDIA("media"),
  INSTAGRAM("instagram");

  /** Playground 저장값이자 공식 홈페이지 응답값. */
  private final String value;

  /** 값이 없거나 알 수 없으면 WEBSITE로 취급한다. */
  public static ProjectLinkType fromValue(String value) {
    return Arrays.stream(values())
        .filter(type -> type.value.equalsIgnoreCase(value))
        .findFirst()
        .orElse(WEBSITE);
  }
}
