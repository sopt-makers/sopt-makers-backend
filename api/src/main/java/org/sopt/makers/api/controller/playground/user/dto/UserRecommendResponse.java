package org.sopt.makers.api.controller.playground.user.dto;

import java.util.List;
import org.sopt.makers.domain.playground.member.profile.MemberRecommendation;
import org.sopt.makers.domain.playground.member.profile.RecommendationType;

public record UserRecommendResponse(List<RecommendedMember> members) {

  public record RecommendedMember(
      Long id, String name, String profileImage, Integer generation, String part, RecommendationType recommendType) {}

  public static UserRecommendResponse from(List<MemberRecommendation> recommendations) {
    List<RecommendedMember> members =
        recommendations.stream()
            .map(
                r ->
                    new RecommendedMember(
                        r.id(), r.name(), r.profileImage(), r.generation(), r.part(), r.type()))
            .toList();
    return new UserRecommendResponse(members);
  }
}
