package org.sopt.makers.domain.user.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.port.PlaygroundProjectUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundProjectUserAdapter implements PlaygroundProjectUserPort {

  private final UserQueryService userQueryService;

  @Override
  public List<ProjectUserInfo> getProjectUserInfosByIds(List<Long> userIds) {
    return userQueryService.findAllWithActivitiesByIds(userIds).stream()
        .map(
            user ->
                new ProjectUserInfo(
                    user.id(),
                    user.profile().name(),
                    user.profile().profileImage(),
                    user.activities().activities().stream()
                        .map(activity -> activity.generation())
                        .toList(),
                    !user.isFirstLogin()))
        .toList();
  }
}
