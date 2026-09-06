package org.sopt.makers.domain.app.home.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.home.AppServiceBadgeInfo;
import org.sopt.makers.domain.app.poke.service.PokeHistoryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PokeBadgeManager implements AppServiceBadgeManager {

  private static final long BADGE_LIMIT = 9L;
  private static final String OVER_LIMIT_BADGE = "9+";

  private final PokeHistoryService pokeHistoryService;

  @Override
  public AppServiceBadgeInfo acquireAppServiceBadgeInfo(Long userId) {
    long unRepliedCount = pokeHistoryService.getUnRepliedPokeMeSize(userId);
    if (unRepliedCount <= 0) {
      return AppServiceBadgeInfo.createWithAllDisabled();
    }
    String badge = unRepliedCount > BADGE_LIMIT ? OVER_LIMIT_BADGE : String.valueOf(unRepliedCount);
    return AppServiceBadgeInfo.createWithEnabledDisplayAlarmBadge(badge);
  }
}
