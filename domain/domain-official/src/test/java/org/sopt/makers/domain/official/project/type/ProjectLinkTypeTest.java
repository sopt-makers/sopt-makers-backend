package org.sopt.makers.domain.official.project.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProjectLinkTypeTest {

  @ParameterizedTest
  @CsvSource({
    "WEBSITE, website",
    "GOOGLE_PLAYSTORE, googlePlay",
    "APP_STORE, appStore",
    "GITHUB, github",
    "MEDIA, media",
    "INSTAGRAM, instagram"
  })
  @DisplayName("링크 종류는 레거시 응답값과 일대일로 대응한다")
  void mapsLegacyValue(ProjectLinkType type, String value) {
    assertThat(type.getValue()).isEqualTo(value);
    assertThat(ProjectLinkType.fromValue(value)).contains(type);
  }

  @ParameterizedTest
  @CsvSource({"GitHub, GITHUB", "GOOGLEPLAY, GOOGLE_PLAYSTORE", "AppStore, APP_STORE"})
  @DisplayName("링크 종류는 대소문자를 구분하지 않고 찾는다")
  void ignoresCase(String value, ProjectLinkType expected) {
    assertThat(ProjectLinkType.fromValue(value)).contains(expected);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"youtube", "GOOGLE_PLAYSTORE"})
  @DisplayName("알 수 없는 링크 종류는 빈 값을 반환한다")
  void returnsEmptyForUnknown(String value) {
    assertThat(ProjectLinkType.fromValue(value)).isEmpty();
  }
}
