package org.sopt.makers.domain.playground.member.profile.service.sorting;

import org.sopt.makers.domain.user.User;
import org.springframework.stereotype.Component;

/** 최신 기수 desc → 프로필 가중치 desc → 이름 asc. */
@Component
public class DefaultMemberComparator implements MemberSortingComparator {

  @Override
  public int compare(User a, User b, ProfileWeightStrategy weightStrategy) {
    int generationCompare =
        Integer.compare(ProfileSortingSupport.lastGeneration(b), ProfileSortingSupport.lastGeneration(a));
    if (generationCompare != 0) {
      return generationCompare;
    }

    int weightCompare = Integer.compare(weightStrategy.calculate(b), weightStrategy.calculate(a));
    if (weightCompare != 0) {
      return weightCompare;
    }

    return ProfileSortingSupport.name(a).compareTo(ProfileSortingSupport.name(b));
  }
}
