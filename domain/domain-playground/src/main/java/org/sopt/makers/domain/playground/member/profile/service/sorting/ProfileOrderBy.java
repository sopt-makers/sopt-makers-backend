package org.sopt.makers.domain.playground.member.profile.service.sorting;

import java.util.Arrays;

public enum ProfileOrderBy {
  LATEST_REGISTERED(1),
  OLDEST_REGISTERED(2),
  LATEST_GENERATION(3),
  OLDEST_GENERATION(4);

  private final int code;

  ProfileOrderBy(int code) {
    this.code = code;
  }

  public static ProfileOrderBy fromCode(Integer code) {
    if (code == null) {
      return null;
    }
    return Arrays.stream(values()).filter(v -> v.code == code).findFirst().orElse(null);
  }
}
