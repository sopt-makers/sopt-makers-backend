package org.sopt.makers.domain.user.adapter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.PokeUserProfile;
import org.sopt.makers.domain.user.RecommendSeed;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.exception.UserException;
import org.sopt.makers.domain.user.exception.UserFailure;
import org.sopt.makers.domain.user.port.AppPokeUserPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppPokeUserAdapter implements AppPokeUserPort {

  private final UserQueryService userQueryService;

  @Override
  public boolean exists(Long userId) {
    return userQueryService.existsById(userId);
  }

  @Override
  public List<Long> filterExisting(Collection<Long> userIds) {
    return userQueryService.filterExistingIds(userIds);
  }

  @Override
  public List<Long> findAllUserIds() {
    return userQueryService.findAllUserIds();
  }

  @Override
  public PokeUserProfile findProfile(Long userId) {
    return toPokeUserProfile(userQueryService.getWithActivitiesById(userId));
  }

  @Override
  public List<PokeUserProfile> findProfiles(Collection<Long> userIds) {
    return userQueryService.getUsers(List.copyOf(userIds)).stream()
        .map(this::toPokeUserProfileLenient)
        .toList();
  }

  @Override
  public RecommendSeed findRecommendSeed(Long userId) {
    User user = userQueryService.getWithActivitiesById(userId);
    return new RecommendSeed(
        user.profile().mbti(),
        user.profile().university(),
        user.activities().activities().stream().map(Activity::generation).toList());
  }

  @Override
  public Set<Long> findUserIdsByRecommendCondition(
      Set<Integer> generations, String mbti, String university) {
    return userQueryService.findUserIdsByRecommendCondition(generations, mbti, university);
  }

  private PokeUserProfile toPokeUserProfile(User user) {
    Activity latest =
        user.activities().activities().stream()
            .max(Comparator.comparingInt(Activity::generation))
            .orElseThrow(() -> new UserException(UserFailure.NOT_FOUND_USER));
    return toProfile(user, latest);
  }

  private PokeUserProfile toPokeUserProfileLenient(User user) {
    return user.activities().activities().stream()
        .max(Comparator.comparingInt(Activity::generation))
        .map(latest -> toProfile(user, latest))
        .orElseGet(
            () ->
                new PokeUserProfile(
                    user.id(), user.profile().name(), user.profile().profileImage(), null, null));
  }

  private PokeUserProfile toProfile(User user, Activity latest) {
    return new PokeUserProfile(
        user.id(),
        user.profile().name(),
        user.profile().profileImage(),
        (long) latest.generation(),
        latest.part() == null ? null : latest.part().getName());
  }
}
