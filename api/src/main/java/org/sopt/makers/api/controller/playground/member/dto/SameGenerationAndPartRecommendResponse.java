package org.sopt.makers.api.controller.playground.member.dto;

import java.util.List;
import org.sopt.makers.domain.playground.member.profile.SameGenerationAndPartMember;

public record SameGenerationAndPartRecommendResponse(List<Member> members) {

  public record Member(Long id, String name, String profileImage, Integer generation, String part) {}

  public static SameGenerationAndPartRecommendResponse from(List<SameGenerationAndPartMember> members) {
    List<Member> responseMembers =
        members.stream()
            .map(m -> new Member(m.id(), m.name(), m.profileImage(), m.generation(), m.part()))
            .toList();
    return new SameGenerationAndPartRecommendResponse(responseMembers);
  }
}
