package org.sopt.makers.api.common.validation;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GraphemeSize 테스트")
class GraphemeSizeTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  @DisplayName("조합형 이모지도 화면에 보이는 한 글자로 센다")
  void countsCombinedEmojiAsOne() {
    assertThat(validator.validate(new TwoGraphemes("👨‍👩‍👧‍👦"))).isNotEmpty();
    assertThat(validator.validate(new TwoGraphemes("👨‍👩‍👧‍👦👍🏽"))).isEmpty();
    assertThat(validator.validate(new TwoGraphemes("👨‍👩‍👧‍👦👍🏽🇰🇷"))).isNotEmpty();
  }

  @Test
  @DisplayName("한글과 영문도 각각 한 글자로 센다")
  void countsPlainCharacters() {
    assertThat(validator.validate(new TwoGraphemes("가나"))).isEmpty();
    assertThat(validator.validate(new TwoGraphemes("ab"))).isEmpty();
    assertThat(validator.validate(new TwoGraphemes("가나다"))).isNotEmpty();
  }

  @Test
  @DisplayName("값이 없으면 검증하지 않는다")
  void skipsNull() {
    assertThat(validator.validate(new TwoGraphemes(null))).isEmpty();
  }

  private record TwoGraphemes(@GraphemeSize(min = 2, max = 2) String value) {}
}
