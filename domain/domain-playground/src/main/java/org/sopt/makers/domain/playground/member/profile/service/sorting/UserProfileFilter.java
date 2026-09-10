package org.sopt.makers.domain.playground.member.profile.service.sorting;

import java.util.Objects;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserCareer;

public final class UserProfileFilter {

  private UserProfileFilter() {}

  public static Part resolvePartFilter(Integer filter) {
    if (filter == null) {
      return null;
    }
    return switch (filter) {
      case 1 -> Part.PLAN;
      case 2 -> Part.DESIGN;
      case 3 -> Part.WEB;
      case 4 -> Part.SERVER;
      case 5 -> Part.ANDROID;
      case 6 -> Part.IOS;
      default -> null;
    };
  }

  /** 팀 필터 중 EXECUTIVE/MAKERS는 activity 단위로만 판정되므로 정렬 전략(운영팀/미디어팀 전용 comparator) 선택에는 쓰이지 않는다. */
  public static Team toSortingTeam(ProfileTeamFilter teamFilter) {
    if (teamFilter == ProfileTeamFilter.OPERATION) {
      return Team.OPERATION;
    }
    if (teamFilter == ProfileTeamFilter.MEDIA) {
      return Team.MEDIA;
    }
    return null;
  }

  public static boolean matchesActivityConditions(
      User user, Part partFilter, ProfileTeamFilter teamFilter, Integer generation) {
    if (partFilter == null && teamFilter == null && generation == null) {
      return true;
    }
    return user.activities().activities().stream()
        .anyMatch(activity -> matches(activity, partFilter, teamFilter, generation));
  }

  public static boolean matchesSearch(User user, String search) {
    if (search == null || search.isBlank()) {
      return true;
    }
    String keyword = search.trim();
    Profile profile = user.profile();

    boolean inName = profile.name() != null && profile.name().contains(keyword);
    boolean inUniversity = profile.university() != null && profile.university().contains(keyword);
    boolean inCompany =
        profile.careers() != null
            && profile.careers().stream()
                .map(UserCareer::companyName)
                .filter(Objects::nonNull)
                .anyMatch(companyName -> companyName.contains(keyword));

    return inName || inUniversity || inCompany;
  }

  private static boolean matches(
      Activity activity, Part partFilter, ProfileTeamFilter teamFilter, Integer generation) {
    boolean generationMatch =
        generation == null || Objects.equals(activity.generation(), generation);
    boolean partMatch = partFilter == null || normalizePart(activity.part()) == partFilter;
    if (!generationMatch || !partMatch) {
      return false;
    }
    if (teamFilter == null) {
      return true;
    }

    return switch (teamFilter) {
        // 레거시에서 "임원진"은 미디어팀/운영팀이 아닌 별도 직책(회장/부회장/파트장 등)을 뜻했다. 새 도메인에서는
        // 팀장(TEAM_LEADER)만 운영팀/미디어팀 소속으로 남고 그 외 비-MEMBER 역할이 그 의미에 대응한다.
      case EXECUTIVE ->
          activity.isSopt()
              && activity.role() != Role.MEMBER
              && activity.role() != Role.TEAM_LEADER;
      case MAKERS -> !activity.isSopt() || activity.team() == Team.MAKERS;
      case OPERATION -> activity.team() == Team.OPERATION;
      case MEDIA -> activity.team() == Team.MEDIA;
    };
  }

  private static Part normalizePart(Part part) {
    if (part == null) {
      return null;
    }
    return switch (part) {
      case PM -> Part.PLAN;
      case FRONTEND -> Part.WEB;
      case BACKEND -> Part.SERVER;
      default -> part;
    };
  }
}
