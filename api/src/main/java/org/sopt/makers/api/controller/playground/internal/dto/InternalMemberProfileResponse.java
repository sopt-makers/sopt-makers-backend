package org.sopt.makers.api.controller.playground.internal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;

public record InternalMemberProfileResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Long memberId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String profileImage,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String introduction,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String mbti,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String university,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "36,서버")
        List<CardinalInfoResponse> activities) {

  public static InternalMemberProfileResponse from(User user) {
    return new InternalMemberProfileResponse(
        user.id(),
        user.profile().name(),
        user.profile().profileImage(),
        user.profile().introduction(),
        user.profile().mbti(),
        user.profile().university(),
        toActivities(user.activities().activities()));
  }

  static List<CardinalInfoResponse> toActivities(List<Activity> activities) {
    return activities.stream()
        .map(
            activity ->
                new CardinalInfoResponse(
                    activity.generation()
                        + ","
                        + (activity.part() == null ? "" : activity.part().getName())))
        .toList();
  }
}
