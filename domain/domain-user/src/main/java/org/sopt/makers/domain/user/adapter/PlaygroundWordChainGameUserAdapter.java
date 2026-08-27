package org.sopt.makers.domain.user.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.port.PlaygroundWordChainGameUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundWordChainGameUserAdapter implements PlaygroundWordChainGameUserPort {

  private final UserQueryService userQueryService;

  @Override
  public List<UserInfo> getUserInfosByIds(List<Long> userIds) {
    return userQueryService.getUsers(userIds).stream()
        .map(user -> new UserInfo(user.id(), user.profile().name(), user.profile().profileImage()))
        .toList();
  }
}
