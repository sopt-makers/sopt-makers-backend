package org.sopt.makers.domain.app.home;

import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;

public enum ActivityStatus {
  ACTIVE,
  INACTIVE,
  UNAUTHENTICATED;

  private static final int NO_SOPT_GENERATION = 0;

  public static ActivityStatus of(User user, long currentGeneration) {
    long lastSoptGeneration =
        user.activities().activities().stream()
            .filter(Activity::isSopt)
            .mapToInt(Activity::generation)
            .max()
            .orElse(NO_SOPT_GENERATION);
    return lastSoptGeneration == currentGeneration ? ACTIVE : INACTIVE;
  }
}
