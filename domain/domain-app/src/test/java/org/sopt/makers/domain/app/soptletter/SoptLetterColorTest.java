package org.sopt.makers.domain.app.soptletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sopt.makers.domain.app.soptletter.SoptLetterColor.BLUE_50;
import static org.sopt.makers.domain.app.soptletter.SoptLetterColor.GREEN_50;
import static org.sopt.makers.domain.app.soptletter.SoptLetterColor.RED_50;
import static org.sopt.makers.domain.app.soptletter.SoptLetterColor.YELLOW_50;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SoptLetterColor 테스트")
class SoptLetterColorTest {

  @Test
  @DisplayName("색은 파랑 초록 노랑 빨강 순으로 돌아간다")
  void rotatesInOrder() {
    assertThat(BLUE_50.next()).isEqualTo(GREEN_50);
    assertThat(GREEN_50.next()).isEqualTo(YELLOW_50);
    assertThat(YELLOW_50.next()).isEqualTo(RED_50);
    assertThat(RED_50.next()).isEqualTo(BLUE_50);
  }

  @Test
  @DisplayName("주제의 첫 메시지는 파랑으로 시작한다")
  void startsWithBlue() {
    assertThat(SoptLetterColor.first()).isEqualTo(BLUE_50);
  }
}
