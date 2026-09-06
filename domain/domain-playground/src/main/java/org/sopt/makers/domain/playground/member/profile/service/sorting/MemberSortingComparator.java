package org.sopt.makers.domain.playground.member.profile.service.sorting;

import org.sopt.makers.domain.user.User;

public interface MemberSortingComparator {
  int compare(User a, User b, ProfileWeightStrategy weightStrategy);
}
