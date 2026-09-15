package org.sopt.makers.domain.app.home.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.home.AppService;
import org.sopt.makers.domain.app.home.AppServiceBadgeInfo;
import org.sopt.makers.domain.app.home.AppServiceEntryStatus;
import org.sopt.makers.domain.app.home.AppServiceName;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppServiceBadgeService {

  private static final String CACHE_SYNC_EXECUTOR = "cacheSyncTaskExecutor";
  private static final String DEFAULT_BADGE_MANAGER = "defaultBadgeManager";

  private final Map<String, AppServiceBadgeManager> badgeManagerMap;

  @Async(CACHE_SYNC_EXECUTOR)
  public CompletableFuture<AppServiceEntryStatus> getAppServiceEntryStatusResponseAsync(
      AppService appService, Long userId) {
    AppServiceBadgeInfo badgeInfo = findBadgeManager(appService).acquireAppServiceBadgeInfo(userId);
    return CompletableFuture.completedFuture(AppServiceEntryStatus.of(appService, badgeInfo));
  }

  private AppServiceBadgeManager findBadgeManager(AppService appService) {
    String managerName = AppServiceName.of(appService.serviceName()).getBadgeManagerName();
    AppServiceBadgeManager manager = badgeManagerMap.get(managerName);
    return manager != null ? manager : badgeManagerMap.get(DEFAULT_BADGE_MANAGER);
  }
}
