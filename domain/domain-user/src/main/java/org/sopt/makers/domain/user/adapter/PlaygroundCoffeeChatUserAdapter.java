package org.sopt.makers.domain.user.adapter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.port.PlaygroundCoffeeChatUserPort;
import org.sopt.makers.domain.user.port.UserCareerRepositoryPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundCoffeeChatUserAdapter implements PlaygroundCoffeeChatUserPort {

  private final UserQueryService userQueryService;
  private final UserCareerRepositoryPort userCareerRepositoryPort;

  @Override
  public UserDetail getUserDetail(Long userId) {
    User user = userQueryService.getWithActivitiesById(userId);
    List<UserCareer> careers = userCareerRepositoryPort.findByUserId(userId);
    return toUserDetail(user, careers);
  }

  @Override
  public List<UserDetail> getUserDetails(List<Long> userIds) {
    if (userIds.isEmpty()) {
      return List.of();
    }
    List<User> users = userQueryService.findAllWithActivitiesByIds(userIds);
    Map<Long, List<UserCareer>> careersByUserId =
        userCareerRepositoryPort.findByUserIdIn(userIds).stream()
            .collect(Collectors.groupingBy(UserCareer::userId));
    return users.stream()
        .map(user -> toUserDetail(user, careersByUserId.getOrDefault(user.id(), List.of())))
        .toList();
  }

  private UserDetail toUserDetail(User user, List<UserCareer> careers) {
    Optional<CareerDetail> lastCareer =
        careers.stream()
            .filter(c -> Boolean.TRUE.equals(c.isCurrent()))
            .findFirst()
            .or(
                () ->
                    careers.stream()
                        .filter(c -> c.id() != null)
                        .max(Comparator.comparingLong(UserCareer::id)))
            .map(c -> new CareerDetail(c.companyName(), c.title()));

    return new UserDetail(
        user.id(),
        user.profile().name(),
        user.profile().profileImage(),
        user.profile().phone(),
        user.profile().email(),
        user.profile().isPhoneBlind(),
        user.profile().university(),
        user.activities().activities().stream()
            .map(a -> new ActivityInfo(a.generation(), a.part() != null ? a.part().name() : "", a.isSopt()))
            .toList(),
        lastCareer);
  }
}
