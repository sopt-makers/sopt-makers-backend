package org.sopt.makers.domain.playground.member.profile.service.sorting;

import org.sopt.makers.domain.user.Team;

/**
 * 목록 조회 team 쿼리 파라미터의 canonical 값. 레거시 클라이언트는 한글 팀명("운영팀"/"미디어팀"/"임원진"/"메이커스")을
 * 그대로 전달하므로 이를 우선 인식하고, 영문 코드("EXECUTIVE" 등)도 하위 호환으로 계속 허용한다. 레거시와 동일하게 이 4개의
 * 코드 외 값(빈 문자열, "해당 없음" 포함)은 필터 미적용으로 취급한다.
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
    if (rawCode.equals("임원진")) {
      return EXECUTIVE;
    }
    if (rawCode.equals(Team.MAKERS.getDisplayName())) {
      return MAKERS;
    }
    if (rawCode.equals(Team.OPERATION.getDisplayName())) {
      return OPERATION;
    }
    if (rawCode.equals(Team.MEDIA.getDisplayName())) {
      return MEDIA;
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
