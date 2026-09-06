package org.sopt.makers.domain.playground.member.profile;

import java.util.List;

public record UserProfileListResult(List<MemberProfileListItem> members, boolean hasNext, int totalCount) {

  public static UserProfileListResult empty() {
    return new UserProfileListResult(List.of(), false, 0);
  }
}
