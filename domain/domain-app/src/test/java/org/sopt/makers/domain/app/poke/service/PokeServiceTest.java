package org.sopt.makers.domain.app.poke.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.poke.exception.PokeFailure.NOT_FOUND_POKE_HISTORY;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.poke.PokeDetail;
import org.sopt.makers.domain.app.poke.PokeEvent;
import org.sopt.makers.domain.app.poke.PokeHistory;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.fake.InMemoryAppPokeUserPort;
import org.sopt.makers.domain.app.poke.fake.InMemoryPokeHistoryRepository;

@DisplayName("PokeService 테스트")
class PokeServiceTest {

  private static final Long POKER = 1L;
  private static final Long POKED = 2L;

  private InMemoryPokeHistoryRepository pokeHistoryRepository;
  private InMemoryAppPokeUserPort pokeUserPort;
  private List<Object> publishedEvents;
  private PokeService pokeService;

  @BeforeEach
  void setUp() {
    pokeHistoryRepository = new InMemoryPokeHistoryRepository();
    pokeUserPort = new InMemoryAppPokeUserPort();
    pokeUserPort.addUser(POKER, "찌른 사람");
    pokeUserPort.addUser(POKED, "찔린 사람");
    publishedEvents = new ArrayList<>();
    pokeService = new PokeService(pokeHistoryRepository, publishedEvents::add);
  }

  @Test
  @DisplayName("찌르면 기록을 저장하고 PokeEvent 를 발행한다")
  void pokePublishesEvent() {
    PokeHistory saved = pokeService.poke(POKER, POKED, "안녕", false);

    assertThat(saved.id()).isNotNull();
    assertThat(saved.message()).isEqualTo("안녕");
    assertThat(saved.isReply()).isFalse();
    assertThat(publishedEvents).containsExactly(new PokeEvent(POKED));
  }

  @Test
  @DisplayName("상대가 나를 찌른 미답장 기록이 있으면 답장 처리한다")
  void pokeMarksOppositeUnRepliedHistory() {
    PokeHistory pokedMe = pokeHistoryRepository.seed(POKED, POKER, false);

    pokeService.poke(POKER, POKED, "안녕", false);

    assertThat(pokeHistoryRepository.get(pokedMe.id()).isReply()).isTrue();
  }

  @Test
  @DisplayName("내가 보낸 미답장 기록은 답장 처리 대상이 아니다")
  void pokeDoesNotMarkMyOwnHistory() {
    PokeHistory mine = pokeHistoryRepository.seed(POKER, POKED, false);

    pokeService.poke(POKER, POKED, "안녕", false);

    assertThat(pokeHistoryRepository.get(mine.id()).isReply()).isFalse();
  }

  @Test
  @DisplayName("찌르기 상세는 저장된 기록의 보낸이, 받은이, 메시지를 담는다")
  void getPokeDetail() {
    PokeHistory saved = pokeService.poke(POKER, POKED, "안녕", true);

    PokeDetail detail = pokeService.getPokeDetail(saved.id());

    assertThat(detail.pokerId()).isEqualTo(POKER);
    assertThat(detail.pokedId()).isEqualTo(POKED);
    assertThat(detail.message()).isEqualTo("안녕");
  }

  @Test
  @DisplayName("없는 찌르기 기록을 조회하면 NOT_FOUND_POKE_HISTORY 예외가 발생한다")
  void getPokeDetailThrowsWhenMissing() {
    assertThatThrownBy(() -> pokeService.getPokeDetail(999L))
        .isInstanceOf(PokeException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_POKE_HISTORY);
  }

  @Test
  @DisplayName("내가 보낸 찌르기 총 횟수를 센다")
  void getUserPokeCount() {
    pokeService.poke(POKER, POKED, "안녕", false);
    pokeService.poke(POKER, POKED, "또 안녕", false);

    assertThat(pokeService.getUserPokeCount(POKER)).isEqualTo(2L);
  }
}
