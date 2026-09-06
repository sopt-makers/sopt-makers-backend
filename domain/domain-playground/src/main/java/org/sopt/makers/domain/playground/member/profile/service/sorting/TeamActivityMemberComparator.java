package org.sopt.makers.domain.playground.member.profile.service.sorting;

import java.util.Comparator;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.User;
import org.springframework.stereotype.Component;

/** 운영팀/미디어팀 필터 전용: 해당 팀 소속 최신 기수 desc → 프로필 가중치 desc → 이름 asc. */
@Component
public class TeamActivityMemberComparator implements MemberSortingComparator {

  private final Team targetTeam;

  public TeamActivityMemberComparator() {
    this.targetTeam = null;
  }

  private TeamActivityMemberComparator(Team targetTeam) {
    this.targetTeam = targetTeam;
  }

  public TeamActivityMemberComparator withTeam(Team team) {
    return new TeamActivityMemberComparator(team);
  }

  @Override
  public int compare(User a, User b, ProfileWeightStrategy weightStrategy) {
    Integer latestTeamGenerationA = latestTeamGeneration(a);
    Integer latestTeamGenerationB = latestTeamGeneration(b);

    if (latestTeamGenerationA == null && latestTeamGenerationB == null) {
      int generationCompare =
          Integer.compare(ProfileSortingSupport.lastGeneration(b), ProfileSortingSupport.lastGeneration(a));
      if (generationCompare != 0) {
        return generationCompare;
      }
    } else if (latestTeamGenerationA == null) {
      return 1;
    } else if (latestTeamGenerationB == null) {
      return -1;
    } else {
      int teamGenerationCompare = Integer.compare(latestTeamGenerationB, latestTeamGenerationA);
      if (teamGenerationCompare != 0) {
        return teamGenerationCompare;
      }
    }

    int weightCompare = Integer.compare(weightStrategy.calculate(b), weightStrategy.calculate(a));
    if (weightCompare != 0) {
      return weightCompare;
    }

    return ProfileSortingSupport.name(a).compareTo(ProfileSortingSupport.name(b));
  }

  private Integer latestTeamGeneration(User user) {
    if (targetTeam == null) {
      return null;
    }
    return user.activities().activities().stream()
        .filter(activity -> targetTeam.equals(activity.team()))
        .map(Activity::generation)
        .max(Comparator.naturalOrder())
        .orElse(null);
  }
}
