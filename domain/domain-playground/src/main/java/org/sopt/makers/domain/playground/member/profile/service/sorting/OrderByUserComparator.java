package org.sopt.makers.domain.playground.member.profile.service.sorting;

import org.sopt.makers.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class OrderByUserComparator implements UserSortingComparator {

  private final ProfileOrderBy orderBy;

  public OrderByUserComparator() {
    this.orderBy = null;
  }

  private OrderByUserComparator(ProfileOrderBy orderBy) {
    this.orderBy = orderBy;
  }

  public OrderByUserComparator withOrderBy(ProfileOrderBy orderBy) {
    return new OrderByUserComparator(orderBy);
  }

  @Override
  public int compare(User a, User b, ProfileWeightStrategy weightStrategy) {
    if (orderBy == null) {
      throw new IllegalStateException("ProfileOrderBy가 설정되지 않았습니다.");
    }

    return switch (orderBy) {
      case LATEST_REGISTERED -> Long.compare(b.id(), a.id());
      case OLDEST_REGISTERED -> Long.compare(a.id(), b.id());
      case LATEST_GENERATION -> compareByGeneration(a, b, weightStrategy, true);
      case OLDEST_GENERATION -> compareByGeneration(a, b, weightStrategy, false);
    };
  }

  private int compareByGeneration(
      User a, User b, ProfileWeightStrategy weightStrategy, boolean descending) {
    int generationA = ProfileSortingSupport.lastGeneration(a);
    int generationB = ProfileSortingSupport.lastGeneration(b);
    int generationCompare =
        descending
            ? Integer.compare(generationB, generationA)
            : Integer.compare(generationA, generationB);
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
