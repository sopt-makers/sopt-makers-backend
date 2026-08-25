package org.sopt.makers.domain.official.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
  PRESIDENT("회장", 1),
  VICE_PRESIDENT("부회장", 2),
  GENERAL_AFFAIRS("총무", 3),
  OPERATION_TEAM_LEADER("운영 팀장", 4),
  MEDIA_TEAM_LEADER("미디어 팀장", 5),
  MAKERS_TEAM_LEADER("메이커스 팀장", 6),
  ART_DIRECTOR("아트디렉터", 7),
  PLANNING_TEAM_LEADER("기획 팀장", 8),
  DESIGN_TEAM_LEADER("디자인 팀장", 9),
  PLAN_LEADER("기획 파트장", 10),
  DESIGN_LEADER("디자인 파트장", 11),
  ANDROID_LEADER("안드로이드 파트장", 12),
  IOS_LEADER("iOS 파트장", 13),
  WEB_LEADER("웹 파트장", 14),
  SERVER_LEADER("서버 파트장", 15),
  MEDIA_TEAM_MEMBER("미디어팀", 16),
  PLANNING_TEAM_MEMBER("기획팀", 17),
  DESIGN_TEAM_MEMBER("디자인팀", 18);

  private final String displayName;
  private final int order;

  public static MemberRole fromString(String role) {
    return fromLegacyRole(role);
  }

  public static MemberRole fromLegacyRole(String role) {
    if (role == null || role.isBlank()) {
      throw new IllegalArgumentException("Role must not be blank");
    }

    return switch (role) {
      case "PRESIDENT", "회장" -> PRESIDENT;
      case "VICE_PRESIDENT", "부회장" -> VICE_PRESIDENT;
      case "GENERAL_AFFAIRS", "총무" -> GENERAL_AFFAIRS;
      case "OPERATION_TEAM_LEADER", "운영 팀장" -> OPERATION_TEAM_LEADER;
      case "MEDIA_TEAM_LEADER", "미디어 팀장" -> MEDIA_TEAM_LEADER;
      case "MAKERS_TEAM_LEADER", "메이커스 팀장" -> MAKERS_TEAM_LEADER;
      case "ART_DIRECTOR", "아트디렉터" -> ART_DIRECTOR;
      case "PLANNING_TEAM_LEADER", "기획", "기획 팀장" -> PLANNING_TEAM_LEADER;
      case "DESIGN_TEAM_LEADER", "디자인", "디자인 팀장" -> DESIGN_TEAM_LEADER;
      case "PLAN_LEADER", "기획 파트장" -> PLAN_LEADER;
      case "DESIGN_LEADER", "디자인 파트장" -> DESIGN_LEADER;
      case "ANDROID_LEADER", "안드로이드", "안드로이드 파트장" -> ANDROID_LEADER;
      case "IOS_LEADER", "iOS", "iOS 파트장" -> IOS_LEADER;
      case "WEB_LEADER", "웹", "웹 파트장" -> WEB_LEADER;
      case "SERVER_LEADER", "서버", "서버 파트장" -> SERVER_LEADER;
      case "MEDIA_TEAM_MEMBER", "미디어팀" -> MEDIA_TEAM_MEMBER;
      case "PLANNING_TEAM_MEMBER", "기획팀" -> PLANNING_TEAM_MEMBER;
      case "DESIGN_TEAM_MEMBER", "디자인팀" -> DESIGN_TEAM_MEMBER;
      default -> throw new IllegalArgumentException("Unknown role: " + role);
    };
  }
}
