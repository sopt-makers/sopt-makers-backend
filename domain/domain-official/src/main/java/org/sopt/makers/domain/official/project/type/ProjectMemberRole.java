package org.sopt.makers.domain.official.project.type;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectMemberRole {
  TEAMLEADER("Team Leader"),
  MAINPM("Main PM"),
  PM("PM"),
  TEAMIMPROVEMENT("Team Improvement"),
  DESIGN("디자이너"),
  IOS("iOS 개발자"),
  ANDROID("Android 개발자"),
  WEB("웹 프론트엔드 개발자"),
  SERVER("서버 개발자");

  /** 공식 홈페이지에 표시하는 역할명. */
  private final String description;

  /** 레거시와 같이 enum 이름, 역할명 순서로 대소문자를 구분하지 않고 찾는다. */
  public static Optional<ProjectMemberRole> fromValue(String value) {
    return Arrays.stream(values())
        .filter(role -> role.name().equalsIgnoreCase(value))
        .findFirst()
        .or(
            () ->
                Arrays.stream(values())
                    .filter(role -> role.description.equalsIgnoreCase(value))
                    .findFirst());
  }
}
