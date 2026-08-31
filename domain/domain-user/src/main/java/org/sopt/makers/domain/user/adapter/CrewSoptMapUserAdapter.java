package org.sopt.makers.domain.user.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.CrewSoptMapUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrewSoptMapUserAdapter implements CrewSoptMapUserPort {

  private final UserQueryService userQueryService;

  @Override
  public Optional<User> findById(Long userId) {
    return userQueryService.findById(userId);
  }

  @Override
  public List<User> findAllByIds(List<Long> userIds) {
    return userQueryService.findAllWithActivitiesByIds(userIds);
  }
}
