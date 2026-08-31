package org.sopt.makers.domain.app.soptletter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SoptLetterTopic 테스트")
class SoptLetterTopicTest {

  private static final LocalDateTime STARTED_AT = LocalDateTime.of(2026, 8, 1, 0, 0);
  private static final LocalDateTime ENDED_AT = LocalDateTime.of(2026, 8, 31, 23, 59);

  @Test
  @DisplayName("노출 기간 안이면 활성이다")
  void activeWithinPeriod() {
    assertThat(topic(STARTED_AT, ENDED_AT).isActiveAt(LocalDateTime.of(2026, 8, 15, 12, 0)))
        .isTrue();
  }

  @Test
  @DisplayName("시작과 종료 시각 당일도 활성이다")
  void activeOnBoundary() {
    assertThat(topic(STARTED_AT, ENDED_AT).isActiveAt(STARTED_AT)).isTrue();
    assertThat(topic(STARTED_AT, ENDED_AT).isActiveAt(ENDED_AT)).isTrue();
  }

  @Test
  @DisplayName("노출 기간 밖이면 비활성이다")
  void inactiveOutsidePeriod() {
    assertThat(topic(STARTED_AT, ENDED_AT).isActiveAt(STARTED_AT.minusSeconds(1))).isFalse();
    assertThat(topic(STARTED_AT, ENDED_AT).isActiveAt(ENDED_AT.plusSeconds(1))).isFalse();
  }

  @Test
  @DisplayName("기간이 비어 있으면 비활성이다")
  void inactiveWhenPeriodMissing() {
    assertThat(topic(null, ENDED_AT).isActiveAt(LocalDateTime.of(2026, 8, 15, 12, 0))).isFalse();
    assertThat(topic(STARTED_AT, null).isActiveAt(LocalDateTime.of(2026, 8, 15, 12, 0))).isFalse();
  }

  private SoptLetterTopic topic(LocalDateTime startedAt, LocalDateTime endedAt) {
    return new SoptLetterTopic(1L, "주제", "CTA", false, startedAt, endedAt, STARTED_AT);
  }
}
