package org.sopt.makers.api.controller.playground.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Comparator;
import java.util.List;
import org.sopt.makers.domain.playground.member.profile.TlMemberCard;
import org.sopt.makers.domain.playground.member.tl.ServiceType;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;

public record TlMemberResponse(
    @Schema(required = true) Long id,
    @Schema(required = true) String name,
    String university,
    String profileImage,
    @Schema(required = true) List<UserProfileResponse.MemberSoptActivityResponse> activities,
    String introduction,
    @Schema(required = true) ServiceType serviceType,
    @Schema(required = true) String selfIntroduction,
    @Schema(required = true) String competitionData) {

  public static TlMemberResponse from(TlMemberCard card) {
    User user = card.user();
    List<UserProfileResponse.MemberSoptActivityResponse> activities =
        user.activities().activities().stream()
            .sorted(Comparator.comparingInt(Activity::generation).thenComparing(a -> !a.isSopt()))
            .map(
                a ->
                    new UserProfileResponse.MemberSoptActivityResponse(
                        a.id(),
                        a.generation(),
                        a.part() == null ? null : a.part().getName(),
                        a.team() == null ? null : a.team().getDisplayName()))
            .toList();

    return new TlMemberResponse(
        user.id(),
        user.profile().name(),
        user.profile().university(),
        user.profile().profileImage(),
        activities,
        user.profile().introduction(),
        card.tlUser().serviceType(),
        card.tlUser().selfIntroduction(),
        card.tlUser().competitionData());
  }
}
