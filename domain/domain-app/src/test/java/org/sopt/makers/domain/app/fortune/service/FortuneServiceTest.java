package org.sopt.makers.domain.app.fortune.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.fortune.exception.FortuneFailure.NOT_FOUND_FORTUNE_FROM_USER;
import static org.sopt.makers.domain.app.fortune.exception.FortuneFailure.NOT_FOUND_FORTUNE_WORD;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.fortune.FortuneCard;
import org.sopt.makers.domain.app.fortune.FortuneWord;
import org.sopt.makers.domain.app.fortune.UserFortune;
import org.sopt.makers.domain.app.fortune.exception.FortuneException;
import org.sopt.makers.domain.app.fortune.port.FortuneCardRepositoryPort;
import org.sopt.makers.domain.app.fortune.port.FortuneWordRepositoryPort;
import org.sopt.makers.domain.app.fortune.port.UserFortuneRepositoryPort;

@DisplayName("FortuneService 테스트")
class FortuneServiceTest {

  private static final LocalDate TODAY = LocalDate.of(2026, 7, 8);
  private static final Long WORD_ID = 100L;

  private InMemoryUserFortuneRepositoryPort userFortuneRepositoryPort;
  private InMemoryFortuneWordRepositoryPort fortuneWordRepositoryPort;
  private InMemoryFortuneCardRepositoryPort fortuneCardRepositoryPort;
  private FortuneService fortuneService;

  @BeforeEach
  void setUp() {
    userFortuneRepositoryPort = new InMemoryUserFortuneRepositoryPort();
    fortuneWordRepositoryPort = new InMemoryFortuneWordRepositoryPort();
    fortuneCardRepositoryPort = new InMemoryFortuneCardRepositoryPort();
    fortuneWordRepositoryPort.add(new FortuneWord(WORD_ID, "오늘의 솝마디", 10L));
    Clock clock = Clock.fixed(Instant.parse("2026-07-08T00:00:00Z"), ZoneOffset.UTC);
    fortuneService =
        new FortuneService(
            fortuneCardRepositoryPort,
            fortuneWordRepositoryPort,
            userFortuneRepositoryPort,
            new FortuneWordIdGenerator(fortuneWordRepositoryPort),
            clock);
  }

  @Test
  @DisplayName("운세가 없던 유저는 오늘자로 새로 배정받고 솝마디를 반환한다")
  void newUserGetsFortuneAssigned() {
    FortuneWord word = fortuneService.getTodayFortuneWord(1L, TODAY);

    assertThat(word.title()).isEqualTo("오늘의 솝마디");
    assertThat(userFortuneRepositoryPort.findByUserId(1L)).isPresent();
    assertThat(userFortuneRepositoryPort.findByUserId(1L).get().checkedAt()).isEqualTo(TODAY);
  }

  @Test
  @DisplayName("어제 확인한 유저는 오늘 날짜로 재배정된다")
  void staleUserGetsRefreshedToToday() {
    userFortuneRepositoryPort.save(UserFortune.create(1L, WORD_ID, LocalDate.of(2026, 7, 1)));

    fortuneService.getTodayFortuneWord(1L, TODAY);

    assertThat(userFortuneRepositoryPort.findByUserId(1L).get().checkedAt()).isEqualTo(TODAY);
  }

  @Test
  @DisplayName("배정된 운세 문구가 없으면 NOT_FOUND_FORTUNE_WORD 예외가 발생한다")
  void throwsWhenFortuneWordMissing() {
    userFortuneRepositoryPort.save(UserFortune.create(1L, 999L, TODAY));

    assertThatThrownBy(() -> fortuneService.getTodayFortuneWord(1L, TODAY))
        .isInstanceOf(FortuneException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_FORTUNE_WORD);
  }

  @Test
  @DisplayName("유저에게 할당된 오늘의 운세카드를 반환한다")
  void returnsTodayFortuneCard() {
    fortuneCardRepositoryPort.setCardForUser(
        1L, new FortuneCard(10L, "카드", "설명", "image.png", "#FFFFFF"));

    FortuneCard card = fortuneService.getTodayFortuneCard(1L);

    assertThat(card.name()).isEqualTo("카드");
  }

  @Test
  @DisplayName("할당된 운세카드가 없으면 NOT_FOUND_FORTUNE_FROM_USER 예외가 발생한다")
  void throwsWhenFortuneCardMissing() {
    assertThatThrownBy(() -> fortuneService.getTodayFortuneCard(1L))
        .isInstanceOf(FortuneException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_FORTUNE_FROM_USER);
  }

  @Test
  @DisplayName("오늘 확인한 유저는 isExistTodayFortune이 true, 아니면 false")
  void isExistTodayFortune() {
    userFortuneRepositoryPort.save(UserFortune.create(1L, WORD_ID, TODAY));
    userFortuneRepositoryPort.save(UserFortune.create(2L, WORD_ID, LocalDate.of(2026, 7, 1)));

    assertThat(fortuneService.isExistTodayFortune(1L)).isTrue();
    assertThat(fortuneService.isExistTodayFortune(2L)).isFalse();
    assertThat(fortuneService.isExistTodayFortune(999L)).isFalse();
  }

  private static final class InMemoryUserFortuneRepositoryPort
      implements UserFortuneRepositoryPort {
    private final Map<Long, UserFortune> store = new HashMap<>();
    private long sequence = 1L;

    @Override
    public Optional<UserFortune> findByUserId(Long userId) {
      return store.values().stream().filter(uf -> uf.userId().equals(userId)).findFirst();
    }

    @Override
    public UserFortune save(UserFortune userFortune) {
      Long id = userFortune.id() == null ? sequence++ : userFortune.id();
      UserFortune saved =
          new UserFortune(
              id, userFortune.userId(), userFortune.fortuneWordId(), userFortune.checkedAt());
      store.put(id, saved);
      return saved;
    }
  }

  private static final class InMemoryFortuneWordRepositoryPort
      implements FortuneWordRepositoryPort {
    private final Map<Long, FortuneWord> store = new LinkedHashMap<>();

    void add(FortuneWord fortuneWord) {
      store.put(fortuneWord.id(), fortuneWord);
    }

    @Override
    public Optional<FortuneWord> findById(Long id) {
      return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Long> findAllIds() {
      return new ArrayList<>(store.keySet());
    }
  }

  private static final class InMemoryFortuneCardRepositoryPort
      implements FortuneCardRepositoryPort {
    private final Map<Long, FortuneCard> cardByUserId = new HashMap<>();

    void setCardForUser(Long userId, FortuneCard fortuneCard) {
      cardByUserId.put(userId, fortuneCard);
    }

    @Override
    public Optional<FortuneCard> findTodayCardByUserId(Long userId) {
      return Optional.ofNullable(cardByUserId.get(userId));
    }
  }
}
