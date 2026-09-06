package org.sopt.makers.domain.app.home;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.home.exception.HomeFailure.NOT_FOUND_USER_GENERATION;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.home.exception.HomeException;

@DisplayName("ActivityDurationCalculator 테스트")
class ActivityDurationCalculatorTest {

  private static final LocalDate TODAY = LocalDate.of(2026, 9, 3);

  @Test
  @DisplayName("짝수 기수는 3월 시작으로 센다")
  void evenGenerationStartsInMarch() {
    assertThat(ActivityDurationCalculator.calculate(List.of(34L), TODAY)).isEqualTo(31);
  }

  @Test
  @DisplayName("홀수 기수는 9월 시작으로 센다")
  void oddGenerationStartsInSeptember() {
    assertThat(ActivityDurationCalculator.calculate(List.of(33L), TODAY)).isEqualTo(37);
  }

  @Test
  @DisplayName("여러 기수면 가장 이른 기수부터 센다")
  void usesEarliestGeneration() {
    assertThat(ActivityDurationCalculator.calculate(List.of(38L, 34L, 36L), TODAY)).isEqualTo(31);
  }

  @Test
  @DisplayName("기수가 없으면 NOT_FOUND_USER_GENERATION 예외가 발생한다")
  void throwsWhenNoGeneration() {
    assertThatThrownBy(() -> ActivityDurationCalculator.calculate(List.of(), TODAY))
        .isInstanceOf(HomeException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_USER_GENERATION);
  }
}
