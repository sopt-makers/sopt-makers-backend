package org.sopt.makers.domain.user.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.port.FortuneUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FortuneUserAdapter implements FortuneUserPort {

  private final UserQueryService userQueryService;

  @Override
  public String getName(Long userId) {
    return userQueryService.getById(userId).profile().name();
  }
}
