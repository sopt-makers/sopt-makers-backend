package org.sopt.makers.domain.playground.community;

/** 커뮤니티 카테고리를 나타내는 canonical 코드. DB category row 및 신규 단일 코드 조회의 기준이 된다. */
public enum CommunityCategoryCode {
  FREE,
  MEETING,

  PROMOTION,
  PROMOTION_EVENT,
  PROMOTION_PROJECT,
  PROMOTION_RECRUIT,
  PROMOTION_ETC,

  SOPTICLE,
  SOPTICLE_PLAN,
  SOPTICLE_DESIGN,
  SOPTICLE_SERVER,
  SOPTICLE_WEB,
  SOPTICLE_IOS,
  SOPTICLE_ANDROID,
  SOPTICLE_ETC
}
