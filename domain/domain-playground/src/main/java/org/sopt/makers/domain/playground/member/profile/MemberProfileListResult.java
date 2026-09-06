package org.sopt.makers.domain.playground.member.profile;

import java.util.List;

public record MemberProfileListResult(List<MemberProfileListItem> members, boolean hasNext, int totalCount) {

  public static MemberProfileListResult empty() {
    return new MemberProfileListResult(List.of(), false, 0);
  }
}
