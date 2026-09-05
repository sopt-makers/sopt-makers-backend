package org.sopt.makers.domain.app.soptamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.Team;

class SoptampPartTest {

  private static final Map<String, String> LEGACY_SHORT_NAMES = new LinkedHashMap<>();

  private static final Map<String, String> LEGACY_PART_NAMES = new LinkedHashMap<>();

  static {
    LEGACY_PART_NAMES.put("PRESIDENT", "회장");
    LEGACY_PART_NAMES.put("VICE_PRESIDENT", "부회장");
    LEGACY_PART_NAMES.put("GENERAL_AFFAIR", "총무");
    LEGACY_PART_NAMES.put("MEDIA_TEAM_LEADER", "미디어 팀장");
    LEGACY_PART_NAMES.put("OPERATIONS_TEAM_LEADER", "운영 팀장");
    LEGACY_PART_NAMES.put("MAKERS_TEAM_LEADER", "메이커스 팀장");
    LEGACY_PART_NAMES.put("ART_DIRECTOR", "아트 디렉터");
    LEGACY_PART_NAMES.put("PLAN", "기획");
    LEGACY_PART_NAMES.put("PLAN_PART_LEADER", "기획 파트장");
    LEGACY_PART_NAMES.put("DESIGN", "디자인");
    LEGACY_PART_NAMES.put("DESIGN_PART_LEADER", "디자인 파트장");
    LEGACY_PART_NAMES.put("ANDROID", "안드로이드");
    LEGACY_PART_NAMES.put("ANDROID_PART_LEADER", "안드로이드 파트장");
    LEGACY_PART_NAMES.put("IOS", "iOS");
    LEGACY_PART_NAMES.put("IOS_PART_LEADER", "iOS 파트장");
    LEGACY_PART_NAMES.put("WEB", "웹");
    LEGACY_PART_NAMES.put("WEB_PART_LEADER", "웹 파트장");
    LEGACY_PART_NAMES.put("SERVER", "서버");
    LEGACY_PART_NAMES.put("SERVER_PART_LEADER", "서버 파트장");
    LEGACY_PART_NAMES.put("NONE", "미상");
    LEGACY_PART_NAMES.put("PM", "PM");
    LEGACY_PART_NAMES.put("FRONTEND", "프론트엔드");
    LEGACY_PART_NAMES.put("BACKEND", "백엔드");
    LEGACY_PART_NAMES.put("MARKETER", "마케터");
    LEGACY_PART_NAMES.put("RESEARCHER", "리서처");
    LEGACY_PART_NAMES.put("ORGANIZER", "오거나이저");
    LEGACY_PART_NAMES.put("CX", "CX");

    LEGACY_SHORT_NAMES.put("PRESIDENT", "회장");
    LEGACY_SHORT_NAMES.put("VICE_PRESIDENT", "부회장");
    LEGACY_SHORT_NAMES.put("GENERAL_AFFAIR", "총무");
    LEGACY_SHORT_NAMES.put("MEDIA_TEAM_LEADER", "미팀장");
    LEGACY_SHORT_NAMES.put("OPERATIONS_TEAM_LEADER", "운팀장");
    LEGACY_SHORT_NAMES.put("MAKERS_TEAM_LEADER", "메팀장");
    LEGACY_SHORT_NAMES.put("ART_DIRECTOR", "아트디렉터");
    LEGACY_SHORT_NAMES.put("PLAN", "기획");
    LEGACY_SHORT_NAMES.put("PLAN_PART_LEADER", "기획파트장");
    LEGACY_SHORT_NAMES.put("DESIGN", "디자인");
    LEGACY_SHORT_NAMES.put("DESIGN_PART_LEADER", "디자인파트장");
    LEGACY_SHORT_NAMES.put("ANDROID", "안드");
    LEGACY_SHORT_NAMES.put("ANDROID_PART_LEADER", "안드파트장");
    LEGACY_SHORT_NAMES.put("IOS", "아요");
    LEGACY_SHORT_NAMES.put("IOS_PART_LEADER", "아요파트장");
    LEGACY_SHORT_NAMES.put("WEB", "웹");
    LEGACY_SHORT_NAMES.put("WEB_PART_LEADER", "웹파트장");
    LEGACY_SHORT_NAMES.put("SERVER", "서버");
    LEGACY_SHORT_NAMES.put("SERVER_PART_LEADER", "서버파트장");
    LEGACY_SHORT_NAMES.put("NONE", "선배");
    LEGACY_SHORT_NAMES.put("PM", "PM");
    LEGACY_SHORT_NAMES.put("FRONTEND", "FE");
    LEGACY_SHORT_NAMES.put("BACKEND", "BE");
    LEGACY_SHORT_NAMES.put("MARKETER", "마케터");
    LEGACY_SHORT_NAMES.put("RESEARCHER", "리서처");
    LEGACY_SHORT_NAMES.put("ORGANIZER", "오거나이저");
    LEGACY_SHORT_NAMES.put("CX", "CX");
  }

  @Test
  @DisplayName("구서버 SoptPart의 값을 빠짐없이 옮겼다")
  void vocabularyMatchesLegacy() {
    assertThat(SoptampPart.values()).hasSize(LEGACY_SHORT_NAMES.size());
  }

  @Test
  @DisplayName("구서버 SoptPart의 약칭을 한 글자도 다르지 않게 옮겼다")
  void shortNamesMatchLegacy() {
    for (Map.Entry<String, String> legacy : LEGACY_SHORT_NAMES.entrySet()) {
      assertThat(SoptampPart.valueOf(legacy.getKey()).getShortName())
          .as("%s 의 약칭", legacy.getKey())
          .isEqualTo(legacy.getValue());
    }
  }

  @Test
  @DisplayName("구서버 SoptPart의 풀네임을 한 글자도 다르지 않게 옮겼다")
  void partNamesMatchLegacy() {
    for (Map.Entry<String, String> legacy : LEGACY_PART_NAMES.entrySet()) {
      assertThat(SoptampPart.valueOf(legacy.getKey()).getPartName())
          .as("%s 의 풀네임", legacy.getKey())
          .isEqualTo(legacy.getValue());
    }
  }

  @Test
  @DisplayName("파트장은 자기 파트로 접혀 파트 랭킹에 함께 잡힌다")
  void partLeaderFoldsIntoOwnPart() {
    assertThat(SoptampPart.SERVER_PART_LEADER.toPart()).isEqualTo(Part.SERVER);
    assertThat(SoptampPart.SERVER_PART_LEADER.getShortName()).startsWith("서버");
  }

  @Test
  @DisplayName("임원진과 파트 없는 값은 집계 축이 없다")
  void executivesHaveNoPart() {
    assertThat(SoptampPart.PRESIDENT.toPart()).isNull();
    assertThat(SoptampPart.MAKERS_TEAM_LEADER.toPart()).isNull();
    assertThat(SoptampPart.NONE.toPart()).isNull();
  }

  @Test
  @DisplayName("활동 이력에서 라벨을 정한다")
  void resolvesFromActivity() {
    assertThat(SoptampPart.of(Part.SERVER, Role.MEMBER, null)).isEqualTo(SoptampPart.SERVER);
    assertThat(SoptampPart.of(Part.SERVER, Role.PART_LEADER, null))
        .isEqualTo(SoptampPart.SERVER_PART_LEADER);
    assertThat(SoptampPart.of(null, Role.PRESIDENT, null)).isEqualTo(SoptampPart.PRESIDENT);
    assertThat(SoptampPart.of(null, Role.TEAM_LEADER, Team.MEDIA))
        .isEqualTo(SoptampPart.MEDIA_TEAM_LEADER);
  }

  @Test
  @DisplayName("아트 디렉터와 메이커스 챕터는 구서버처럼 NONE으로 접힌다")
  void unsupportedFoldsToNone() {
    assertThat(SoptampPart.of(Part.DESIGN, Role.ART_DIRECTOR, null)).isEqualTo(SoptampPart.NONE);
    assertThat(SoptampPart.of(Part.BACKEND, Role.MEMBER, null)).isEqualTo(SoptampPart.NONE);
    assertThat(SoptampPart.of(Part.PM, Role.MEMBER, null)).isEqualTo(SoptampPart.NONE);
    assertThat(SoptampPart.of(null, Role.TEAM_LEADER, null)).isEqualTo(SoptampPart.NONE);
  }

  @Test
  @DisplayName("저장된 값을 읽을 때 모르는 문자열은 NONE으로 본다")
  void fromUnknownNameIsNone() {
    assertThat(SoptampPart.from("SERVER_PART_LEADER")).isEqualTo(SoptampPart.SERVER_PART_LEADER);
    assertThat(SoptampPart.from("BACKEND")).isEqualTo(SoptampPart.BACKEND);
    assertThat(SoptampPart.from("무엇인가")).isEqualTo(SoptampPart.NONE);
    assertThat(SoptampPart.from(null)).isEqualTo(SoptampPart.NONE);
  }

  @Test
  @DisplayName("랭킹을 제공하지 않는 파트로 조회하면 막는다")
  void rankingPartIsLimitedToActiveParts() {
    assertThat(SoptampPart.of(Part.IOS).getShortName()).isEqualTo("아요");
    assertThatThrownBy(() -> SoptampPart.of(Part.BACKEND)).isInstanceOf(SoptampException.class);
  }
}
