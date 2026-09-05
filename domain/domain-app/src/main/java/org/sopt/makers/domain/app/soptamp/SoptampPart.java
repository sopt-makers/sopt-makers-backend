package org.sopt.makers.domain.app.soptamp;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.Team;

@Getter
@RequiredArgsConstructor
public enum SoptampPart {
  PRESIDENT("회장", "회장", null),
  VICE_PRESIDENT("부회장", "부회장", null),
  GENERAL_AFFAIR("총무", "총무", null),
  MEDIA_TEAM_LEADER("미디어 팀장", "미팀장", null),
  OPERATIONS_TEAM_LEADER("운영 팀장", "운팀장", null),
  MAKERS_TEAM_LEADER("메이커스 팀장", "메팀장", null),
  ART_DIRECTOR("아트 디렉터", "아트디렉터", null),

  PLAN("기획", "기획", Part.PLAN),
  PLAN_PART_LEADER("기획 파트장", "기획파트장", Part.PLAN),
  DESIGN("디자인", "디자인", Part.DESIGN),
  DESIGN_PART_LEADER("디자인 파트장", "디자인파트장", Part.DESIGN),
  ANDROID("안드로이드", "안드", Part.ANDROID),
  ANDROID_PART_LEADER("안드로이드 파트장", "안드파트장", Part.ANDROID),
  IOS("iOS", "아요", Part.IOS),
  IOS_PART_LEADER("iOS 파트장", "아요파트장", Part.IOS),
  WEB("웹", "웹", Part.WEB),
  WEB_PART_LEADER("웹 파트장", "웹파트장", Part.WEB),
  SERVER("서버", "서버", Part.SERVER),
  SERVER_PART_LEADER("서버 파트장", "서버파트장", Part.SERVER),

  NONE("미상", "선배", null),

  PM("PM", "PM", null),
  FRONTEND("프론트엔드", "FE", null),
  BACKEND("백엔드", "BE", null),
  MARKETER("마케터", "마케터", null),
  RESEARCHER("리서처", "리서처", null),
  ORGANIZER("오거나이저", "오거나이저", null),
  CX("CX", "CX", null);

  private final String partName;

  private final String shortName;
  private final Part part;

  private static final Set<Part> UNSUPPORTED_PARTS =
      EnumSet.of(
          Part.BACKEND,
          Part.FRONTEND,
          Part.PM,
          Part.MARKETER,
          Part.RESEARCHER,
          Part.ORGANIZER,
          Part.CX);

  public static SoptampPart of(Part part, Role role, Team team) {
    if (role == null) {
      return ofPart(part);
    }
    return switch (role) {
      case PRESIDENT -> PRESIDENT;
      case VICE_PRESIDENT -> VICE_PRESIDENT;
      case GENERAL_AFFAIRS -> GENERAL_AFFAIR;
      case ART_DIRECTOR -> NONE;
      case TEAM_LEADER -> ofTeam(team);
      case PART_LEADER -> ofPartLeader(part);
      default -> ofPart(part);
    };
  }

  public static SoptampPart of(Part part) {
    SoptampPart soptampPart = ofPart(part);
    if (soptampPart == NONE) {
      throw new SoptampException(SoptampFailure.UNSUPPORTED_RANKING_PART);
    }
    return soptampPart;
  }

  public static SoptampPart findSoptPartByPartName(String partName) {
    return Arrays.stream(values())
        .filter(soptPart -> soptPart.partName.equalsIgnoreCase(partName))
        .findAny()
        .orElse(NONE);
  }

  public static SoptampPart from(String name) {
    return Arrays.stream(values()).filter(it -> it.name().equals(name)).findFirst().orElse(NONE);
  }

  public Part toPart() {
    return part;
  }

  public boolean isSoptPart() {
    return part != null;
  }

  private static SoptampPart ofPart(Part part) {
    if (part == null || UNSUPPORTED_PARTS.contains(part)) {
      return NONE;
    }
    return switch (part) {
      case PLAN -> PLAN;
      case DESIGN -> DESIGN;
      case ANDROID -> ANDROID;
      case IOS -> IOS;
      case WEB -> WEB;
      case SERVER -> SERVER;
      default -> NONE;
    };
  }

  private static SoptampPart ofPartLeader(Part part) {
    return switch (ofPart(part)) {
      case PLAN -> PLAN_PART_LEADER;
      case DESIGN -> DESIGN_PART_LEADER;
      case ANDROID -> ANDROID_PART_LEADER;
      case IOS -> IOS_PART_LEADER;
      case WEB -> WEB_PART_LEADER;
      case SERVER -> SERVER_PART_LEADER;
      default -> NONE;
    };
  }

  private static SoptampPart ofTeam(Team team) {
    if (team == null) {
      return NONE;
    }
    return switch (team) {
      case MAKERS -> MAKERS_TEAM_LEADER;
      case MEDIA -> MEDIA_TEAM_LEADER;
      case OPERATION -> OPERATIONS_TEAM_LEADER;
    };
  }
}
