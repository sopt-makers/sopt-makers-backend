package org.sopt.makers.api.controller.user.dto;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDate;
import java.util.List;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;

@NoArgsConstructor(access = PRIVATE)
public final class UserResponse {

  public record UserProfileAndActivity(
      Long userId,
      String name,
      String profileImage,
      LocalDate birthday,
      String phone,
      String email,
      Integer lastGeneration,
      List<SoptActivityDetail> soptActivities) {

    public static UserProfileAndActivity of(User user) {
      List<SoptActivityDetail> activities =
          user.activities().activities().stream().map(SoptActivityDetail::of).toList();
      int lastGeneration = user.activities().getLastSoptActivity().generation();
      return new UserProfileAndActivity(
          user.id(),
          user.profile().name(),
          user.profile().profileImage(),
          user.profile().birthday(),
          user.profile().phone(),
          user.profile().email(),
          lastGeneration,
          activities);
    }

    public static List<UserProfileAndActivity> of(List<User> users) {
      return users.stream().map(UserProfileAndActivity::of).toList();
    }
  }

  public record SoptActivityDetail(
      Long activityId, int generation, String part, String team, String role, boolean isSopt) {

    public static SoptActivityDetail of(Activity activity) {
      return new SoptActivityDetail(
          activity.id(),
          activity.generation(),
          activity.part().getName(),
          activity.team() != null ? activity.team().getDisplayName() : null,
          activity.role().name(),
          activity.isSopt());
    }
  }

  public record PaginatedUserProfiles(
      List<UserProfileAndActivity> profiles, boolean hasNext, long totalCount) {

    public static PaginatedUserProfiles of(List<User> content, boolean hasNext, long totalCount) {
      List<UserProfileAndActivity> profiles =
          content.stream().map(UserProfileAndActivity::of).toList();
      return new PaginatedUserProfiles(profiles, hasNext, totalCount);
    }
  }

  public record UserCountByGeneration(int numberOfMembersAtGeneration) {}
}
