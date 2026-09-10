package org.sopt.makers.domain.playground.member.profile.service.sorting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserCareer;

class UserSortingServiceTest {

  private final UserSortingService service =
      new UserSortingService(
          new DefaultProfileWeightStrategy(),
          new EmployedProfileWeightStrategy(),
          new DefaultUserComparator(),
          new EmployedUserComparator(),
          new TeamActivityUserComparator(),
          new OrderByUserComparator());

  @Test
  @DisplayName("기본 정렬(orderBy 없음, employed 없음)은 최신 기수 desc를 최우선한다")
  void defaultComparatorSortsByGenerationDescendingFirst() {
    User olderGeneration = user(1L, "가", 38, false, 0);
    User newerGeneration = user(2L, "나", 39, false, 0);

    List<User> sorted =
        sort(List.of(olderGeneration, newerGeneration), service.createComparator(null, null));

    assertThat(sorted).extracting(User::id).containsExactly(2L, 1L);
  }

  @Test
  @DisplayName("기본 정렬은 기수가 같으면 프로필 가중치 desc로 tie-break한다")
  void defaultComparatorBreaksGenerationTieByWeight() {
    User noProfileImage = user(1L, "가", 39, false, 0);
    User withProfileImage = user(2L, "나", 39, true, 0);

    List<User> sorted =
        sort(List.of(noProfileImage, withProfileImage), service.createComparator(null, null));

    assertThat(sorted).extracting(User::id).containsExactly(2L, 1L);
  }

  @Test
  @DisplayName("기본 정렬은 기수와 가중치가 같으면 이름 asc로 tie-break한다")
  void defaultComparatorBreaksWeightTieByNameAscending() {
    User named나 = user(1L, "나", 39, false, 0);
    User named가 = user(2L, "가", 39, false, 0);

    List<User> sorted = sort(List.of(named나, named가), service.createComparator(null, null));

    assertThat(sorted).extracting(User::id).containsExactly(2L, 1L);
  }

  @Test
  @DisplayName("employed=1이면 기수와 무관하게 재직 가중치 desc로만 정렬한다")
  void employedComparatorIgnoresGenerationAndUsesEmployedWeight() {
    User higherGenerationNoCareer = user(1L, "가", 40, false, 0);
    User lowerGenerationTwoCareers = user(2L, "나", 38, false, 2);

    Comparator<User> defaultComparator = service.createComparator(null, null);
    Comparator<User> employedComparator = service.createComparator(1, null);

    List<User> defaultSorted =
        sort(List.of(higherGenerationNoCareer, lowerGenerationTwoCareers), defaultComparator);
    List<User> employedSorted =
        sort(List.of(higherGenerationNoCareer, lowerGenerationTwoCareers), employedComparator);

    assertThat(defaultSorted).extracting(User::id).containsExactly(1L, 2L);
    assertThat(employedSorted).extracting(User::id).containsExactly(2L, 1L);
  }

  @Test
  @DisplayName("Default 전략은 프로필 사진에 +5, 경력 1건당 +3을 부여한다")
  void defaultWeightStrategyWeighsProfileImageOverCareer() {
    ProfileWeightStrategy defaultStrategy = new DefaultProfileWeightStrategy();
    User profileImageOnly = user(1L, "가", 39, true, 0);
    User oneCareerOnly = user(2L, "나", 39, false, 1);

    // phone은 헬퍼에서 항상 채워지므로 +1이 공통으로 더해진다.
    assertThat(defaultStrategy.calculate(profileImageOnly)).isEqualTo(5 + 1);
    assertThat(defaultStrategy.calculate(oneCareerOnly)).isEqualTo(3 + 1);
  }

  @Test
  @DisplayName("Employed 전략은 프로필 사진에 +3, 경력 1건당 +5를 부여해 Default와 가중치가 반대로 뒤집힌다")
  void employedWeightStrategyWeighsCareerOverProfileImage() {
    ProfileWeightStrategy employedStrategy = new EmployedProfileWeightStrategy();
    User profileImageOnly = user(1L, "가", 39, true, 0);
    User oneCareerOnly = user(2L, "나", 39, false, 1);

    assertThat(employedStrategy.calculate(profileImageOnly)).isEqualTo(3 + 1);
    assertThat(employedStrategy.calculate(oneCareerOnly)).isEqualTo(5 + 1);
  }

  private List<User> sort(List<User> users, Comparator<User> comparator) {
    List<User> copy = new ArrayList<>(users);
    copy.sort(comparator);
    return copy;
  }

  private User user(
      long id, String name, int generation, boolean hasProfileImage, int careerCount) {
    Profile baseProfile =
        Profile.of(name, null, "010-0000-0000", null, hasProfileImage ? "image-url" : null);
    Profile profile =
        baseProfile.update(
            null,
            "010-0000-0000",
            hasProfileImage ? "image-url" : null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            careers(id, careerCount));

    ActivityList activities =
        ActivityList.of(List.of(Activity.of(generation, Team.MAKERS, null, true)));
    return new User(id, profile, null, activities, false);
  }

  private List<UserCareer> careers(long userId, int count) {
    List<UserCareer> careers = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      careers.add(UserCareer.of((long) i, userId, "회사" + i, "직함", null, null, true));
    }
    return careers;
  }
}
