package org.sopt.makers.domain.user.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.SoptampUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoptampUserAdapter implements SoptampUserPort {

  private final UserQueryService userQueryService;

  @Override
  public Optional<User> findWithActivitiesById(Long userId) {
    return userQueryService.findWithActivitiesById(userId);
  }

  @Override
  public List<User> findAllWithActivitiesByIds(List<Long> userIds) {
    return userQueryService.findAllWithActivitiesByIds(userIds);
  }
}
