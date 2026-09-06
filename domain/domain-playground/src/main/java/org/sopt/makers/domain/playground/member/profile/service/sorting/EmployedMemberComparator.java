package org.sopt.makers.domain.playground.member.profile.service.sorting;

import org.sopt.makers.domain.user.User;
import org.springframework.stereotype.Component;

/** 재직중 필터 전용: 프로필 가중치 desc → 이름 asc. 기수는 비교 대상이 아니다. */
@Component
public class EmployedMemberComparator implements MemberSortingComparator {

  @Override
  public int compare(User a, User b, ProfileWeightStrategy weightStrategy) {
    int weightCompare = Integer.compare(weightStrategy.calculate(b), weightStrategy.calculate(a));
    if (weightCompare != 0) {
      return weightCompare;
    }

    return ProfileSortingSupport.name(a).compareTo(ProfileSortingSupport.name(b));
  }
}
