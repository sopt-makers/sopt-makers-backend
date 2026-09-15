package org.sopt.makers.domain.app.soptamp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.rank.SoptampScoreCacheSyncEvent;
import org.sopt.makers.domain.app.soptamp.support.InMemoryRankCache;
import org.sopt.makers.domain.app.soptamp.support.InMemorySoptampUserStore;

@DisplayName("SoptampUserService 테스트")
class SoptampUserServiceTest {

  private static final long CURRENT_GENERATION = 38L;

  private final InMemorySoptampUserStore store = new InMemorySoptampUserStore(CURRENT_GENERATION);
  private final InMemoryRankCache cache = new InMemoryRankCache();
  private final List<Object> published = new ArrayList<>();

  @Test
  @DisplayName("점수를 더하면 현재 기수 유저는 캐시 동기화 이벤트가 나간다")
  void publishesEventOnAddPoint() {
    SoptampUserService service = service(false);
    store.save(user(1L, CURRENT_GENERATION, 10L));

    service.addPointByLevel(1L, 3);

    assertThat(store.findByUserId(1L).orElseThrow().totalPoints()).isEqualTo(13L);
    assertThat(published).containsExactly(new SoptampScoreCacheSyncEvent(1L));
  }

  @Test
  @DisplayName("점수를 빼거나 초기화해도 같은 이벤트가 나간다")
  void publishesEventOnSubtractAndInit() {
    SoptampUserService service = service(false);
    store.save(user(1L, CURRENT_GENERATION, 10L));

    service.subtractPointByLevel(1L, 3);
    service.initPoint(1L);

    assertThat(store.findByUserId(1L).orElseThrow().totalPoints()).isZero();
    assertThat(published)
        .containsExactly(new SoptampScoreCacheSyncEvent(1L), new SoptampScoreCacheSyncEvent(1L));
  }

  @Test
  @DisplayName("앱잼 모드면 점수는 바뀌어도 캐시 동기화 이벤트는 나가지 않는다")
  void skipsEventInAppjamMode() {
    SoptampUserService service = service(true);
    store.save(user(1L, CURRENT_GENERATION, 10L));

    service.addPointByLevel(1L, 3);

    assertThat(store.findByUserId(1L).orElseThrow().totalPoints()).isEqualTo(13L);
    assertThat(published).isEmpty();
  }

  @Test
  @DisplayName("지난 기수 유저는 캐시가 기수별 키라 이벤트를 내지 않는다")
  void skipsEventForPastGeneration() {
    SoptampUserService service = service(false);
    store.save(user(1L, CURRENT_GENERATION - 1, 10L));

    service.addPointByLevel(1L, 3);

    assertThat(published).isEmpty();
  }

  @Test
  @DisplayName("기수가 없는 행이어도 NPE 없이 건너뛴다")
  void skipsEventForNullGeneration() {
    SoptampUserService service = service(false);
    store.save(user(1L, null, 10L));

    service.addPointByLevel(1L, 3);

    assertThat(published).isEmpty();
  }

  @Test
  @DisplayName("솝탬프 유저가 없으면 조회에서 막힌다")
  void failsWhenUserMissing() {
    SoptampUserService service = service(false);

    assertThatThrownBy(() -> service.getSoptampUser(1L))
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.NOT_FOUND_SOPTAMP_USER.getMessage());
  }

  @Test
  @DisplayName("한마디는 검증 없이 그대로 저장되고 바뀐 값을 돌려준다")
  void editsProfileMessageWithoutValidation() {
    SoptampUserService service = service(false);
    store.save(user(1L, CURRENT_GENERATION, 10L));

    SoptampUser edited = service.editProfileMessage(1L, "   ");

    assertThat(edited.profileMessage()).isEqualTo("   ");
    assertThat(store.findByUserId(1L).orElseThrow().profileMessage()).isEqualTo("   ");
    assertThat(published).isEmpty();
  }

  @Test
  @DisplayName("솝탬프 유저 통삭제는 점수 캐시도 함께 비운다")
  void deletesAllUsersWithScoreCache() {
    SoptampUserService service = service(false);
    store.save(user(1L, CURRENT_GENERATION, 10L));
    cache.put(1L, 10L);

    service.deleteAllSoptampUsers();

    assertThat(store.findAllUserIds()).isEmpty();
    assertThat(cache.getRanking()).isEmpty();
    assertThat(cache.calls()).containsExactly("clearScores");
  }

  @Test
  @DisplayName("점수 전체 초기화는 기수를 가리지 않고 0 으로 만들고 캐시는 건드리지 않는다")
  void initsAllPointsRegardlessOfGeneration() {
    SoptampUserService service = service(false);
    store.save(user(1L, CURRENT_GENERATION, 10L));
    store.save(user(2L, CURRENT_GENERATION - 1, 20L));
    cache.put(1L, 10L);

    service.initAllPoints();

    assertThat(store.findByUserId(1L).orElseThrow().totalPoints()).isZero();
    assertThat(store.findByUserId(2L).orElseThrow().totalPoints()).isZero();
    assertThat(cache.calls()).isEmpty();
  }

  private SoptampUserService service(boolean appjamMode) {
    return new SoptampUserService(
        store,
        store,
        store,
        cache,
        new SoptampMode(appjamMode),
        published::add,
        CURRENT_GENERATION);
  }

  private static SoptampUser user(Long userId, Long generation, Long totalPoints) {
    return new SoptampUser(
        userId, userId, "", totalPoints, "서버홍길동", generation, SoptampPart.SERVER);
  }
}
