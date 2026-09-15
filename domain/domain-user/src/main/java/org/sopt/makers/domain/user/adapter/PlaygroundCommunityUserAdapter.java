package org.sopt.makers.domain.user.adapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.port.PlaygroundCommunityUserPort;
import org.sopt.makers.domain.user.port.UserCareerRepositoryPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundCommunityUserAdapter implements PlaygroundCommunityUserPort {

  private final UserQueryService userQueryService;
  private final UserCareerRepositoryPort userCareerRepositoryPort;

  @Override
  public List<CommunityMemberInfo> getCommunityMemberInfosByIds(List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return List.of();
    }

    List<User> users = userQueryService.findAllWithActivitiesByIds(userIds);

    Map<Long, UserCareer> lastCareerByUserId =
        userCareerRepositoryPort.findLastCareersByUserIds(userIds).stream()
            .collect(Collectors.toMap(UserCareer::userId, career -> career));

    return users.stream()
        .map(user -> toCommunityMemberInfo(user, lastCareerByUserId.get(user.id())))
        .toList();
  }

  private CommunityMemberInfo toCommunityMemberInfo(User user, UserCareer lastCareer) {
    List<ActivityInfo> activityInfos =
        user.activities().activities().stream()
            .map(
                activity ->
                    new ActivityInfo(
                        activity.generation(),
                        activity.part() == null ? null : activity.part().getName(),
                        activity.team() == null ? null : activity.team().getDisplayName(),
                        activity.isSopt()))
            .toList();

    return new CommunityMemberInfo(
        user.id(),
        user.profile().name(),
        user.profile().profileImage(),
        activityInfos,
        lastCareer == null ? null : new CareerInfo(lastCareer.companyName(), lastCareer.title()));
  }
}
