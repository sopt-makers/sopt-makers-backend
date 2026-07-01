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
    if (role == null || role.isBlank()) {
      throw new IllegalArgumentException("Member role must not be blank");
    }
    for (MemberRole memberRole : values()) {
      if (memberRole.name().equalsIgnoreCase(role) || memberRole.displayName.equals(role)) {
        return memberRole;
      }
    }
    throw new IllegalArgumentException("Unknown member role: " + role);
  }
}
