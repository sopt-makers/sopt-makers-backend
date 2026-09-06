package org.sopt.makers.domain.app.home.service;

import org.sopt.makers.domain.app.home.AppServiceBadgeInfo;
import org.springframework.stereotype.Component;

@Component
public class DefaultBadgeManager implements AppServiceBadgeManager {

  @Override
  public AppServiceBadgeInfo acquireAppServiceBadgeInfo(Long userId) {
    return AppServiceBadgeInfo.createWithAllDisabled();
  }
}
