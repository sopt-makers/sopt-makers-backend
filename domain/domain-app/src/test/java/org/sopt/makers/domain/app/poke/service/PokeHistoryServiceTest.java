package org.sopt.makers.domain.app.poke.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.poke.exception.PokeFailure.DUPLICATE_POKE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.poke.PokeHistory;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.fake.InMemoryPokeHistoryRepository;

@DisplayName("PokeHistoryService 테스트")
class PokeHistoryServiceTest {

  private static final Long POKER = 1L;
  private static final Long POKED = 2L;

  private InMemoryPokeHistoryRepository pokeHistoryRepository;
  private PokeHistoryService pokeHistoryService;

  @BeforeEach
  void setUp() {
    pokeHistoryRepository = new InMemoryPokeHistoryRepository();
    pokeHistoryService = new PokeHistoryService(pokeHistoryRepository);
  }

  @Test
  @DisplayName("내가 보낸 미답장 기록이 남아 있으면 DUPLICATE_POKE 예외가 발생한다")
  void checkDuplicateThrowsWhenUnRepliedExists() {
    pokeHistoryRepository.seed(POKER, POKED, false);

    assertThatThrownBy(() -> pokeHistoryService.checkDuplicate(POKER, POKED))
        .isInstanceOf(PokeException.class)
        .extracting("error")
        .isEqualTo(DUPLICATE_POKE);
  }

  @Test
  @DisplayName("내가 보낸 기록이 모두 답장 처리되었으면 중복이 아니다")
  void checkDuplicatePassesWhenAllReplied() {
    pokeHistoryRepository.seed(POKER, POKED, true);

    assertThatCode(() -> pokeHistoryService.checkDuplicate(POKER, POKED))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("상대가 나를 찌른 미답장 기록은 중복 판정에 쓰이지 않는다")
  void checkDuplicateIgnoresOppositeDirection() {
    pokeHistoryRepository.seed(POKED, POKER, false);

    assertThatCode(() -> pokeHistoryService.checkDuplicate(POKER, POKED))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("두 유저 사이 미답장 기록은 방향에 상관없이 최신순으로 조회한다")
  void getAllOfPokeBetween() {
    pokeHistoryRepository.seed(POKER, POKED, false);
    pokeHistoryRepository.seed(POKED, POKER, false);
    pokeHistoryRepository.seed(POKER, POKED, true);

    assertThat(pokeHistoryService.getAllOfPokeBetween(POKER, POKED))
        .extracting(PokeHistory::pokerId)
        .containsExactly(POKED, POKER);
  }

  @Test
  @DisplayName("나를 찌른 유저 id 는 중복 없이 모은다")
  void getPokeMeUserIds() {
    pokeHistoryRepository.seed(POKED, POKER, false);
    pokeHistoryRepository.seed(POKED, POKER, true);
    pokeHistoryRepository.seed(3L, POKER, false);

    assertThat(pokeHistoryService.getPokeMeUserIds(POKER)).containsExactly(POKED, 3L);
  }

  @Test
  @DisplayName("미답장으로 나를 찌른 기록 수를 센다")
  void getUnRepliedPokeMeSize() {
    pokeHistoryRepository.seed(POKED, POKER, false);
    pokeHistoryRepository.seed(3L, POKER, false);
    pokeHistoryRepository.seed(3L, POKER, true);

    assertThat(pokeHistoryService.getUnRepliedPokeMeSize(POKER)).isEqualTo(2L);
  }
}
