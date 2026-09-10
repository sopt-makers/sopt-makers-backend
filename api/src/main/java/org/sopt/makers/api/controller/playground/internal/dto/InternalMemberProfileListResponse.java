package org.sopt.makers.api.controller.playground.internal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.user.User;

public record InternalMemberProfileListResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Long memberId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String profileImage,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String introduction,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "36,서버")
        List<CardinalInfoResponse> activities) {

  public static InternalMemberProfileListResponse from(User user) {
    return new InternalMemberProfileListResponse(
        user.id(),
        user.profile().name(),
        user.profile().profileImage(),
        user.profile().introduction(),
        InternalMemberProfileResponse.toActivities(user.activities().activities()));
  }
}
