package org.sopt.makers.domain.app.home.service;

import org.sopt.makers.domain.app.home.AppServiceBadgeInfo;

public interface AppServiceBadgeManager {

  AppServiceBadgeInfo acquireAppServiceBadgeInfo(Long userId);
}
