package org.sopt.makers.domain.crew.dining.result;

import org.sopt.makers.domain.crew.dining.model.DiningUser;

public record DiningLeader(Long id, String name, String profileImage) {

  public static DiningLeader from(DiningUser user) {
    return new DiningLeader(user.getId(), user.getName(), user.getProfileImage());
  }
}
