package org.sopt.makers.domain.user;

public enum Role {
  MEMBER,
  PRESIDENT,
  VICE_PRESIDENT,
  TEAM_LEADER,
  PART_LEADER,
  GENERAL_AFFAIRS,
  ART_DIRECTOR;

  public boolean isPartRequired() {
    return !(this == PRESIDENT || this == VICE_PRESIDENT);
  }
}
