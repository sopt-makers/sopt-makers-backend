package org.sopt.makers.domain.user;

import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_ACTIVITY_GENERATION;
import static org.sopt.makers.domain.user.exception.UserFailure.ROLE_REQUIRES_PART;

import java.util.Optional;
import lombok.Getter;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.user.exception.UserException;

@Getter
public class Activity {

  private final Long id;
  private final int generation;
  private final Team team;
  private final Part part;
  private final Role role;
  private final boolean isSopt;
  private final Float attendanceScore;

  private Activity(
      Long id,
      int generation,
      Team team,
      Part part,
      Role role,
      boolean isSopt,
      Float attendanceScore) {
    if (generation < 1) {
      throw new UserException(INVALID_ACTIVITY_GENERATION);
    }
    this.id = id;
    this.generation = generation;
    this.team = team;
    this.part = part;
    this.role = role;
    this.isSopt = isSopt;
    this.attendanceScore = attendanceScore;
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

  public Optional<Team> optionalTeam() {
    return Optional.ofNullable(team);
  }

  public void validateActivityContents() {
    if (role.isPartRequired() && part == null) {
      throw new UserException(ROLE_REQUIRES_PART);
    }
  }
}
