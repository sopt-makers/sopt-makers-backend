package org.sopt.makers.domain.app.home.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.home.AppServiceBadgeInfo;
import org.sopt.makers.domain.app.poke.fake.InMemoryPokeHistoryRepository;
import org.sopt.makers.domain.app.poke.service.PokeHistoryService;

@DisplayName("PokeBadgeManager 테스트")
class PokeBadgeManagerTest {

  private static final Long ME = 1L;

  private InMemoryPokeHistoryRepository pokeHistoryRepository;
  private PokeBadgeManager pokeBadgeManager;

  @BeforeEach
  void setUp() {
    pokeHistoryRepository = new InMemoryPokeHistoryRepository();
    pokeBadgeManager = new PokeBadgeManager(new PokeHistoryService(pokeHistoryRepository));
  }

  @Test
  @DisplayName("나를 찌른 미답장이 없으면 뱃지를 끈다")
  void disabledWhenNoUnreplied() {
    pokeHistoryRepository.seed(2L, ME, true);

    assertThat(pokeBadgeManager.acquireAppServiceBadgeInfo(ME))
        .isEqualTo(AppServiceBadgeInfo.createWithAllDisabled());
  }

  @Test
  @DisplayName("미답장 수를 뱃지 문구로 준다")
  void showsUnrepliedCount() {
    pokeHistoryRepository.seed(2L, ME, false);
    pokeHistoryRepository.seed(3L, ME, false);
    pokeHistoryRepository.seed(ME, 4L, false);

    AppServiceBadgeInfo badge = pokeBadgeManager.acquireAppServiceBadgeInfo(ME);

    assertThat(badge.displayAlarmBadge()).isTrue();
    assertThat(badge.alarmBadge()).isEqualTo("2");
  }

  @Test
  @DisplayName("아홉을 넘으면 9+ 로 준다")
  void capsAtNinePlus() {
    for (long poker = 2L; poker <= 12L; poker++) {
      pokeHistoryRepository.seed(poker, ME, false);
    }

    assertThat(pokeBadgeManager.acquireAppServiceBadgeInfo(ME).alarmBadge()).isEqualTo("9+");
  }
}
