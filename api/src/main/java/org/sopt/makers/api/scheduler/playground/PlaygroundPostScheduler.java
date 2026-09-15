package org.sopt.makers.api.scheduler.playground;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.playground.service.PlaygroundPostRefreshService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "sopt.playground.post-refresh-scheduler.enabled",
    havingValue = "true")
public class PlaygroundPostScheduler {

  private final PlaygroundPostRefreshService playgroundPostRefreshService;

  @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
  public void refreshPlaygroundPosts() {
    playgroundPostRefreshService.refreshRecentPosts();
    playgroundPostRefreshService.refreshPopularPosts();
  }
}
