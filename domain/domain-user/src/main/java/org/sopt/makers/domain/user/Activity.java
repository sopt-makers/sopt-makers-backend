package org.sopt.makers.domain.user;

import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_ACTIVITY_GENERATION;
import static org.sopt.makers.domain.user.exception.UserFailure.ROLE_REQUIRES_PART;

import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.user.exception.UserException;

public record Activity(
    Long id,
    int generation,
    Team team,
    Part part,
    Role role,
    boolean isSopt,
    Float attendanceScore) {
  public Activity {
    if (generation < 1) {
      throw new UserException(INVALID_ACTIVITY_GENERATION);
    }
  }

  public static Activity of(int generation, Team team, Part part, boolean isSopt) {
    return new Activity(null, generation, team, part, Role.MEMBER, isSopt, null);
  }

  public static Activity of(
      Long id,
      int generation,
      Team team,
      Part part,
      Role role,
      boolean isSopt,
      Float attendanceScore) {
    return new Activity(id, generation, team, part, role, isSopt, attendanceScore);
  }

  public void validateActivityContents() {
    if (role.isPartRequired() && part == null) {
      throw new UserException(ROLE_REQUIRES_PART);
    }
  }
}
