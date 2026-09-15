package org.sopt.makers.domain.playground.member.profile.service.sorting;

import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;

final class ProfileSortingSupport {

  private ProfileSortingSupport() {}

  static int lastGeneration(User user) {
    return user.activities().activities().stream().mapToInt(Activity::generation).max().orElse(0);
  }

  static String name(User user) {
    return user.profile().name();
  }
}
