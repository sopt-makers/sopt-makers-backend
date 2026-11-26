package org.sopt.makers.domain.app.home.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.home.AppServiceBadgeInfo;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoptLetterBadgeManager implements AppServiceBadgeManager {

  private static final long BADGE_LIMIT = 99L;
  private static final String OVER_LIMIT_BADGE = "99+";

  private final SoptLetterService soptLetterService;

  @Override
  public AppServiceBadgeInfo acquireAppServiceBadgeInfo(Long userId) {
    long unreadCount = soptLetterService.getTotalUnreadCount(userId);
    if (unreadCount <= 0) {
      return AppServiceBadgeInfo.createWithAllDisabled();
    }
    String badge = unreadCount > BADGE_LIMIT ? OVER_LIMIT_BADGE : String.valueOf(unreadCount);
    return AppServiceBadgeInfo.createWithEnabledDisplayAlarmBadge(badge);
  }
}
