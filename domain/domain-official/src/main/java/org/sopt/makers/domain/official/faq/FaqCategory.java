package org.sopt.makers.domain.official.faq;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FaqCategory {
  QUALIFICATION("지원자격", false),
  INTERVIEW("면접", false),
  ACTIVITY("활동", false),

  // 레거시 필드. 후에 삭제 조치 예정
  ALL("전체", true),
  IOS("iOS", true),
  PLAN("기획", true),
  DESIGN("디자인", true),
  SERVER("서버", true),
  ANDROID("안드로이드", true),
  WEB("웹", true),
  COMMON("공통", true);
  private final String displayName;
  private final boolean part;
}
