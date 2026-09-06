package org.sopt.makers.domain.app.playground.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPostRefreshEvent;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPostRefreshEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundPostEventListener {

  private static final String CACHE_SYNC_EXECUTOR = "cacheSyncTaskExecutor";

  private final PlaygroundPostRefreshService playgroundPostRefreshService;

  @Async(CACHE_SYNC_EXECUTOR)
  @EventListener
  public void handleRecentPostRefresh(PlaygroundRecentPostRefreshEvent event) {
    playgroundPostRefreshService.refreshRecentPosts();
  }

  @Async(CACHE_SYNC_EXECUTOR)
  @EventListener
  public void handlePopularPostRefresh(PlaygroundPopularPostRefreshEvent event) {
    playgroundPostRefreshService.refreshPopularPosts();
  }
}
