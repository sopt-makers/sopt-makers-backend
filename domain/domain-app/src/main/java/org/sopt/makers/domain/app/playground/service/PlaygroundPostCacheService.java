package org.sopt.makers.domain.app.playground.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;
import org.sopt.makers.domain.app.playground.port.PlaygroundPostCacheRepositoryPort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaygroundPostCacheService {

  private final PlaygroundPostCacheRepositoryPort playgroundPostCacheRepositoryPort;

  public Optional<List<PlaygroundRecentPost>> getCachedRecentPosts() {
    try {
      return playgroundPostCacheRepositoryPort.getCachedRecentPosts();
    } catch (Exception e) {
      log.warn("Playground 최신 게시글 캐시 조회 실패", e);
      return Optional.empty();
    }
  }

  public Optional<List<PlaygroundPopularPost>> getCachedPopularPosts() {
    try {
      return playgroundPostCacheRepositoryPort.getCachedPopularPosts();
    } catch (Exception e) {
      log.warn("Playground 인기 게시글 캐시 조회 실패", e);
      return Optional.empty();
    }
  }

  public void cacheRecentPosts(List<PlaygroundRecentPost> posts) {
    try {
      playgroundPostCacheRepositoryPort.setCachedRecentPosts(posts);
    } catch (Exception e) {
      log.warn("Playground 최신 게시글 캐시 저장 실패", e);
    }
  }

  public void cachePopularPosts(List<PlaygroundPopularPost> posts) {
    try {
      playgroundPostCacheRepositoryPort.setCachedPopularPosts(posts);
    } catch (Exception e) {
      log.warn("Playground 인기 게시글 캐시 저장 실패", e);
    }
  }
}
