package org.sopt.makers.domain.playground.member.profile.service.sorting;

import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.User;
import org.springframework.stereotype.Service;

/** 필터 조건에 따라 적절한 정렬 전략을 선택하고 {@link Comparator}를 제공한다. */
@Service
@RequiredArgsConstructor
public class UserSortingService {

  private final DefaultProfileWeightStrategy defaultWeightStrategy;
  private final EmployedProfileWeightStrategy employedWeightStrategy;
  private final DefaultUserComparator defaultComparator;
  private final EmployedUserComparator employedComparator;
  private final TeamActivityUserComparator teamActivityComparator;
  private final OrderByUserComparator orderByMemberComparator;

  /** orderBy가 없을 때: team(운영팀/미디어팀) → employed → 기본 정렬 순으로 우선한다. */
  public Comparator<User> createComparator(Integer employed, Team teamFilter) {
    ProfileWeightStrategy weightStrategy = selectWeightStrategy(employed);
    UserSortingComparator sortingComparator = selectSortingComparator(employed, teamFilter);
    return (a, b) -> sortingComparator.compare(a, b, weightStrategy);
  }

  /** orderBy가 있을 때: 필터별 정렬 정책보다 우선 적용한다. */
  public Comparator<User> createComparatorByOrderCondition(
      ProfileOrderBy orderBy, Integer employed) {
    if (orderBy == null) {
      throw new IllegalArgumentException("ProfileOrderBy는 null일 수 없습니다.");
    }
    ProfileWeightStrategy weightStrategy = selectWeightStrategy(employed);
    UserSortingComparator comparator = orderByMemberComparator.withOrderBy(orderBy);
    return (a, b) -> comparator.compare(a, b, weightStrategy);
  }

  private ProfileWeightStrategy selectWeightStrategy(Integer employed) {
    return isEmployedFilter(employed) ? employedWeightStrategy : defaultWeightStrategy;
  }

  private UserSortingComparator selectSortingComparator(Integer employed, Team teamFilter) {
    if (isTeamActivityFilter(teamFilter)) {
      return teamActivityComparator.withTeam(teamFilter);
    }
    if (isEmployedFilter(employed)) {
      return employedComparator;
    }
    return defaultComparator;
  }

  private boolean isEmployedFilter(Integer employed) {
    return employed != null && employed == 1;
  }

  private boolean isTeamActivityFilter(Team teamFilter) {
    return teamFilter == Team.OPERATION || teamFilter == Team.MEDIA;
  }
}
