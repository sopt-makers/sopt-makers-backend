package org.sopt.makers.domain.official.project.type;

import java.util.Arrays;
import java.util.Optional;
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

  /** 레거시와 같이 대소문자를 구분하지 않고 찾는다. */
  public static Optional<ProjectLinkType> fromValue(String value) {
    return Arrays.stream(values()).filter(type -> type.value.equalsIgnoreCase(value)).findFirst();
  }
}
