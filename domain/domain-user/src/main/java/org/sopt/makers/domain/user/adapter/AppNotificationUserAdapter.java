package org.sopt.makers.domain.user.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.port.AppNotificationUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppNotificationUserAdapter implements AppNotificationUserPort {

  private final UserQueryService userQueryService;

  @Override
  public List<Long> findAllUserIds() {
    return userQueryService.findAllUserIds();
  }
}
