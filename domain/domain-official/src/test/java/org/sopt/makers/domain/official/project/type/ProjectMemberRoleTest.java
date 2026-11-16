package org.sopt.makers.domain.official.project.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProjectMemberRoleTest {

  @ParameterizedTest
  @CsvSource({
    "TEAMLEADER, Team Leader",
    "MAINPM, Main PM",
    "PM, PM",
    "TEAMIMPROVEMENT, Team Improvement",
    "DESIGN, 디자이너",
    "IOS, iOS 개발자",
    "ANDROID, Android 개발자",
    "WEB, 웹 프론트엔드 개발자",
    "SERVER, 서버 개발자"
  })
  @DisplayName("역할은 레거시 역할명과 일대일로 대응하고, 이름과 역할명 모두로 찾는다")
  void mapsLegacyDescription(ProjectMemberRole role, String description) {
    assertThat(role.getDescription()).isEqualTo(description);
    assertThat(ProjectMemberRole.fromValue(role.name())).contains(role);
    assertThat(ProjectMemberRole.fromValue(description)).contains(role);
  }

  @ParameterizedTest
  @CsvSource({"design, DESIGN", "Server, SERVER", "TEAM LEADER, TEAMLEADER", "ios 개발자, IOS"})
  @DisplayName("역할은 대소문자를 구분하지 않고 찾는다")
  void ignoresCase(String value, ProjectMemberRole expected) {
    assertThat(ProjectMemberRole.fromValue(value)).contains(expected);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"PLAN", "기획자"})
  @DisplayName("알 수 없는 역할은 빈 값을 반환한다")
  void returnsEmptyForUnknown(String value) {
    assertThat(ProjectMemberRole.fromValue(value)).isEmpty();
  }
}
