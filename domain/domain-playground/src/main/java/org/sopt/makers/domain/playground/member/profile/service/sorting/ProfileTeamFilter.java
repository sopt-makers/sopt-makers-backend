package org.sopt.makers.domain.playground.member.profile.service.sorting;

/**
 * 목록 조회 team 쿼리 파라미터의 canonical 값. 레거시와 동일하게 이 4개의 코드 외 값(빈 문자열, "해당 없음" 포함)은 필터
 * 미적용으로 취급한다.
 */
public enum ProfileTeamFilter {
  EXECUTIVE,
  MAKERS,
  OPERATION,
  MEDIA;

  public static ProfileTeamFilter fromRawCode(String rawCode) {
    if (rawCode == null) {
      return null;
    }
    return switch (rawCode) {
      case "EXECUTIVE" -> EXECUTIVE;
      case "MAKERS" -> MAKERS;
      case "OPERATION" -> OPERATION;
      case "MEDIA" -> MEDIA;
      default -> null;
    };
  }
}
