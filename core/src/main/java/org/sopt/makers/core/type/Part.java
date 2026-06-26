package org.sopt.makers.core.type;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Part {
  ANDROID("안드로이드"),
  IOS("iOS"),
  SERVER("서버"),
  DESIGN("디자인"),
  PLAN("기획"),
  WEB("웹"),
  COMMON("공통"),

  // SOPT MAKERS 챕터
  PM("PM"),
  FRONTEND("프론트엔드"),
  BACKEND("백엔드"),
  MARKETER("마케터"),
  RESEARCHER("리서처"),
  ORGANIZER("오거나이저"),
  CX("CX");

  private final String name;

  public static Part findPart(final String part) {
    if (part == null || part.isEmpty()) {
      throw new IllegalArgumentException("OAuth 플랫폼은 필수 값입니다.");
    }

    return Arrays.stream(Part.values())
        .filter(p -> p.name().equalsIgnoreCase(part))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파트입니다: " + part));
  }
}
