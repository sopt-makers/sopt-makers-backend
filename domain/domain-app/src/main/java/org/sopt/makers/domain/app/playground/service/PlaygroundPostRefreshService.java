package org.sopt.makers.domain.app.playground.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.sopt.makers.domain.app.playground.port.AppHomePlaygroundPostQueryPort;
import org.sopt.makers.domain.app.playground.port.PlaygroundPostCacheRepositoryPort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaygroundPostRefreshService {

  private static final ZoneId KST = ZoneId.of("Asia/Seoul");

  private final AppHomePlaygroundPostQueryPort appHomePlaygroundPostQueryPort;
  private final PlaygroundPostCacheRepositoryPort playgroundPostCacheRepositoryPort;
  private final PlaygroundPostCacheService playgroundPostCacheService;
  private final OperationConfigService operationConfigService;
  private final Clock clock;

  public void refreshRecentPosts() {
    if (!playgroundPostCacheRepositoryPort.tryLockRecentRefresh()) {
      log.error("Playground 최신 게시글 캐시 갱신 락 획득 실패");
      return;
    }
    try {
      Map<String, String> imageConfigMap =
          operationConfigService.getValuesByCategory(OperationConfigCategory.PLAYGROUND_POST);
      LocalDateTime now = LocalDateTime.now(clock.withZone(KST));
      List<PlaygroundRecentPost> posts =
          appHomePlaygroundPostQueryPort.getPlaygroundRecentPosts().stream()
              .map(post -> post.resolveOutdated(imageConfigMap, now))
              .toList();
      playgroundPostCacheService.cacheRecentPosts(posts);
      log.info("Playground 최신 게시글 캐시 갱신 완료");
    } catch (Exception e) {
      log.error("Playground 최신 게시글 캐시 갱신 중 오류 발생", e);
    }
  }

  public void refreshPopularPosts() {
    if (!playgroundPostCacheRepositoryPort.tryLockPopularRefresh()) {
      log.error("Playground 인기 게시글 캐시 갱신 락 획득 실패");
      return;
    }
    try {
      List<PlaygroundPopularPost> posts =
          appHomePlaygroundPostQueryPort.getPlaygroundPopularPosts();
      playgroundPostCacheService.cachePopularPosts(posts);
      log.info("Playground 인기 게시글 캐시 갱신 완료");
    } catch (Exception e) {
      log.error("Playground 인기 게시글 캐시 갱신 중 오류 발생", e);
    }
  }
}
