package org.sopt.makers.domain.user.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.AppHomeUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppHomeUserAdapter implements AppHomeUserPort {

  private final UserQueryService userQueryService;

  @Override
  public User getWithActivities(Long userId) {
    return userQueryService.getWithActivitiesById(userId);
  }
}
