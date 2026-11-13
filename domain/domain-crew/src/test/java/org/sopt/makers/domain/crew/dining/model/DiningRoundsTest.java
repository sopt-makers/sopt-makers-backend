package org.sopt.makers.domain.crew.dining.model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiningRoundsTest {

  private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 23, 12, 0);

  private DiningRound round(Long id, LocalDateTime createdTimestamp) {
    return new DiningRound(
        id,
        1L,
        null,
        null,
        null,
        null,
        org.sopt.makers.domain.crew.dining.model.enums.DiningRoundStatus.OPEN,
        null,
        false,
        false,
        false,
        createdTimestamp);
  }

  @Test
  @DisplayName("회차 번호는 id 오름차순으로 계산하고, 중간 회차가 빠지면 뒤 번호가 당겨진다.")
  void getRoundNumber_isOrderedById() {
    DiningRounds rounds =
        new DiningRounds(List.of(round(30L, NOW), round(10L, NOW), round(50L, NOW)));

    assertThat(rounds.getRoundNumber(10L)).isEqualTo(1);
    assertThat(rounds.getRoundNumber(30L)).isEqualTo(2);
    assertThat(rounds.getRoundNumber(50L)).isEqualTo(3);
    assertThat(rounds.isFirstRound(rounds.getRound(10L))).isTrue();
  }

  @Test
  @DisplayName("NEW 는 1차를 제외한 최신 회차가 생성 24시간 이내일 때만 표시된다.")
  void isNewRound_onlyLatestNonFirstRoundWithin24Hours() {
    DiningRound first = round(1L, NOW.minusHours(1L));
    DiningRound second = round(2L, NOW.minusHours(23L));
    DiningRound third = round(3L, NOW.minusHours(1L));

    // 1차만 있으면 NEW 없음
    assertThat(new DiningRounds(List.of(first)).isNewRound(first, NOW)).isFalse();

    // 최신 회차(3차)만 NEW, 24시간 이내인 2차라도 최신이 아니면 NEW 아님
    DiningRounds rounds = new DiningRounds(List.of(first, second, third));
    assertThat(rounds.isNewRound(third, NOW)).isTrue();
    assertThat(rounds.isNewRound(second, NOW)).isFalse();
    assertThat(rounds.isNewRound(first, NOW)).isFalse();
  }

  @Test
  @DisplayName("최신 회차라도 생성 후 24시간이 지나면 NEW 가 아니다.")
  void isNewRound_falseAfter24Hours() {
    DiningRound first = round(1L, NOW.minusDays(3L));
    DiningRound justBefore = round(2L, NOW.minusHours(24L).plusSeconds(1L));
    DiningRound exactly24h = round(3L, NOW.minusHours(24L));

    assertThat(new DiningRounds(List.of(first, justBefore)).isNewRound(justBefore, NOW)).isTrue();
    assertThat(new DiningRounds(List.of(first, exactly24h)).isNewRound(exactly24h, NOW)).isFalse();
  }
}
