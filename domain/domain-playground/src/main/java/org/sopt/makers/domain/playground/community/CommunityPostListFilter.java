package org.sopt.makers.domain.playground.community;

// API 요청 모델(레거시). 신규 단일 코드(CommunityCategoryCode) 도입 이후에도 하위호환을 위해 유지한다.
public enum CommunityPostListFilter {
  ALL,

  // PROMOTION
  EVENT,
  PROJECT,
  RECRUIT,

  // SOPTICLE
  PLAN,
  DESIGN,
  SERVER,
  WEB,
  IOS,
  ANDROID,

  // SHARED
  ETC
}
