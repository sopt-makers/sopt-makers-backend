package org.sopt.makers.domain.user.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.port.PlaygroundReviewUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundReviewUserAdapter implements PlaygroundReviewUserPort {

  private final UserQueryService userQueryService;

  @Override
  public int getLastGeneration(Long userId) {
    return userQueryService
        .getWithActivitiesById(userId)
        .activities()
        .getLastSoptActivity()
        .generation();
  }
}
