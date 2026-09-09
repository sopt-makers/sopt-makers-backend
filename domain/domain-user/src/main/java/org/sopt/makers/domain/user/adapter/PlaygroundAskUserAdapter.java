package org.sopt.makers.domain.user.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.PlaygroundAskUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundAskUserAdapter implements PlaygroundAskUserPort {

  private final UserQueryService userQueryService;

  @Override
  public String getName(Long userId) {
    return userQueryService.getById(userId).profile().name();
  }

  @Override
  public String getPhoneNumber(Long userId) {
    return userQueryService.getById(userId).profile().phone();
  }

  @Override
  public int getLastSoptGeneration(Long userId) {
    return userQueryService
        .getWithActivitiesById(userId)
        .activities()
        .getLastSoptActivity()
        .generation();
  }

  @Override
  public List<User> findAllWithActivitiesByIds(List<Long> userIds) {
    return userQueryService.findAllWithActivitiesByIds(userIds);
  }
}
