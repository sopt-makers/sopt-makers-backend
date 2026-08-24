package org.sopt.makers.domain.user.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.port.PlaygroundResolutionUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundResolutionUserAdapter implements PlaygroundResolutionUserPort {

  private final UserQueryService userQueryService;

  @Override
  public boolean existsById(Long userId) {
    return userQueryService.findWithActivitiesById(userId).isPresent();
  }

  @Override
  public boolean hasActivities(Long userId) {
    return userQueryService
        .findWithActivitiesById(userId)
        .map(user -> user.activities().getTotalActivitySize() > 0)
        .orElse(false);
  }

  @Override
  public int getLastGeneration(Long userId) {
    return userQueryService
        .getWithActivitiesById(userId)
        .activities()
        .getLastSoptActivity()
        .generation();
  }
}
