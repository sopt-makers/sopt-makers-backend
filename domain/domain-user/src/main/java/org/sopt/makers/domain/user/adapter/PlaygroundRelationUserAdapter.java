package org.sopt.makers.domain.user.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.port.PlaygroundRelationUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundRelationUserAdapter implements PlaygroundRelationUserPort {

  private final UserQueryService userQueryService;

  @Override
  public boolean existsById(Long userId) {
    return userQueryService.existsById(userId);
  }
}
