package org.sopt.makers.api.controller.playground.user.dto;

import java.util.List;
import org.sopt.makers.domain.playground.member.profile.MakersUserProfile;

public record MakersUserProfileResponse(
    Long id,
    String name,
    String profileImage,
    List<MemberSoptActivityResponse> activities,
    List<MemberCareerResponse> careers) {

  public record MemberSoptActivityResponse(Long id, Integer generation) {}

  public record MemberCareerResponse(Long id, String companyName, String title, Boolean isCurrent) {}

  public static MakersUserProfileResponse from(MakersUserProfile profile) {
    List<MemberSoptActivityResponse> activities =
        profile.activities().stream().map(a -> new MemberSoptActivityResponse(a.id(), a.generation())).toList();

    List<MemberCareerResponse> careers =
        profile.careers().stream()
            .map(c -> new MemberCareerResponse(c.id(), c.companyName(), c.title(), c.isCurrent()))
            .toList();

    return new MakersUserProfileResponse(profile.id(), profile.name(), profile.profileImage(), activities, careers);
  }
}
