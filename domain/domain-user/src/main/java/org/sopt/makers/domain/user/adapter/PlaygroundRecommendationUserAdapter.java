package org.sopt.makers.domain.user.adapter;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.WorkPreference;
import org.sopt.makers.domain.user.port.PlaygroundRecommendationUserPort;
import org.sopt.makers.domain.user.port.UserWorkPreferenceRepositoryPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundRecommendationUserAdapter implements PlaygroundRecommendationUserPort {

  private final UserQueryService userQueryService;
  private final UserWorkPreferenceRepositoryPort userWorkPreferenceRepositoryPort;

  @Override
  public List<Long> findUserIdsByActivity(Integer generation, Part part, boolean isSopt) {
    return userQueryService.findUserIdsWithProfileByActivity(generation, part, isSopt);
  }

  @Override
  public List<Long> findUserIdsByMbti(String mbti) {
    return userQueryService.findUserIdsWithProfileByMbti(mbti);
  }

  @Override
  public List<Long> findUserIdsByUniversity(String university) {
    return userQueryService.findUserIdsWithProfileByUniversity(university);
  }

  @Override
  public List<Long> findUserIdsWithWorkPreference() {
    return userQueryService.findUserIdsWithProfileAndWorkPreference();
  }

  @Override
  public List<RecommendationUserInfo> findRecommendationUserInfosByIds(List<Long> userIds) {
    List<User> users = userQueryService.findAllWithActivitiesByIds(userIds);
    Map<Long, WorkPreference> workPreferenceByUserId =
        userWorkPreferenceRepositoryPort.findAllByUserIds(userIds);

    return users.stream()
        .filter(user -> !user.isFirstLogin())
        .map(
            user ->
                new RecommendationUserInfo(
                    user.id(),
                    user.profile().name(),
                    user.profile().profileImage(),
                    user.profile().university(),
                    user.profile().mbti(),
                    workPreferenceByUserId.get(user.id()),
                    user.activities().activities()))
        .toList();
  }
}
