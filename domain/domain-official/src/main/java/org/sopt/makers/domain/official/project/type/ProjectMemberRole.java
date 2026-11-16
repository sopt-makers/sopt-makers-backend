package org.sopt.makers.domain.official.project.type;

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
}
