package org.sopt.makers.domain.app.playground.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.operationconfig.OperationConfig;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.port.OperationConfigPort;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.sopt.makers.domain.app.playground.fake.FakeAppHomePlaygroundPostQueryPort;
import org.sopt.makers.domain.app.playground.fake.InMemoryPlaygroundPostCacheRepositoryPort;

@DisplayName("PlaygroundPostRefreshService 테스트")
class PlaygroundPostRefreshServiceTest {

  private FakeAppHomePlaygroundPostQueryPort queryPort;
  private InMemoryPlaygroundPostCacheRepositoryPort cachePort;
  private PlaygroundPostRefreshService refreshService;

  @BeforeEach
  void setUp() {
    queryPort = new FakeAppHomePlaygroundPostQueryPort();
    cachePort = new InMemoryPlaygroundPostCacheRepositoryPort();
    InMemoryOperationConfigPort configPort = new InMemoryOperationConfigPort();
    configPort.add(
        OperationConfigCategory.PLAYGROUND_POST, "unknown.imageUrl", "https://img/u.png");
    Clock clock = Clock.fixed(Instant.parse("2026-09-03T03:00:00Z"), ZoneOffset.UTC);
    refreshService =
        new PlaygroundPostRefreshService(
            queryPort,
            cachePort,
            new PlaygroundPostCacheService(cachePort),
            new OperationConfigService(configPort),
            clock);
  }

  @Test
  @DisplayName("최신 게시글은 락을 잡으면 outdated 변환을 거쳐 캐시에 저장한다")
  void refreshesRecentPostsWhenLocked() {
    queryPort.setRecent(List.of(recentPost("2026-07-01 10:00:00.000000")));

    refreshService.refreshRecentPosts();

    assertThat(cachePort.getCachedRecentPosts()).isPresent();
    assertThat(cachePort.getCachedRecentPosts().get().getFirst().isOutdated()).isTrue();
    assertThat(cachePort.getCachedRecentPosts().get().getFirst().profileImage())
        .isEqualTo("https://img/u.png");
  }

  @Test
  @DisplayName("최신 게시글은 락을 못 잡으면 조회도 저장도 하지 않는다")
  void skipsRecentWhenLockHeld() {
    cachePort.lockRecent();

    refreshService.refreshRecentPosts();

    assertThat(queryPort.calls()).isZero();
    assertThat(cachePort.getCachedRecentPosts()).isEmpty();
  }

  @Test
  @DisplayName("인기 게시글은 락을 잡으면 그대로 캐시에 저장한다")
  void refreshesPopularPostsWhenLocked() {
    queryPort.setPopular(List.of(popularPost()));

    refreshService.refreshPopularPosts();

    assertThat(cachePort.getCachedPopularPosts()).contains(List.of(popularPost()));
  }

  @Test
  @DisplayName("인기 게시글은 락을 못 잡으면 조회도 저장도 하지 않는다")
  void skipsPopularWhenLockHeld() {
    cachePort.lockPopular();

    refreshService.refreshPopularPosts();

    assertThat(queryPort.calls()).isZero();
    assertThat(cachePort.getCachedPopularPosts()).isEmpty();
  }

  @Test
  @DisplayName("플레이그라운드 조회가 실패해도 예외를 전파하지 않는다")
  void swallowsQueryFailure() {
    queryPort.fail();

    refreshService.refreshRecentPosts();

    assertThat(cachePort.getCachedRecentPosts()).isEmpty();
  }

  private static PlaygroundRecentPost recentPost(String createdAt) {
    return new PlaygroundRecentPost(
        1L,
        10L,
        "https://img/p.png",
        "홍길동",
        "38기 서버",
        "기타",
        "제목",
        "내용",
        "https://pg/1",
        createdAt,
        false);
  }

  private static PlaygroundPopularPost popularPost() {
    return new PlaygroundPopularPost(
        2L, 20L, "https://img/q.png", "김솝트", "38기 웹", 1, "자유", "인기글", "본문", "https://pg/2");
  }

  private static final class InMemoryOperationConfigPort implements OperationConfigPort {
    private final List<OperationConfig> store = new ArrayList<>();

    void add(OperationConfigCategory category, String key, String value) {
      store.add(OperationConfig.text(category, key, value, "test"));
    }

    @Override
    public List<OperationConfig> findAllByCategory(OperationConfigCategory category) {
      return store.stream().filter(c -> c.category() == category).toList();
    }

    @Override
    public Optional<OperationConfig> findByCategoryAndKey(
        OperationConfigCategory category, String key) {
      return store.stream()
          .filter(c -> c.category() == category && c.key().equals(key))
          .findFirst();
    }

    @Override
    public OperationConfig save(OperationConfig operationConfig) {
      store.add(operationConfig);
      return operationConfig;
    }
  }
}
