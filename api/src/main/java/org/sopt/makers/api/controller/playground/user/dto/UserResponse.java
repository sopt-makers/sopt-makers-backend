package org.sopt.makers.api.controller.playground.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.playground.member.profile.MemberSummary;

public record UserResponse(
    @Schema(required = true) Long id,
    @Schema(required = true) String name,
    @Schema(required = true) Integer generation,
    String profileImage,
    @Schema(required = true) Boolean hasProfile,
    @Schema(required = true) Boolean editActivitiesAble) {

  public static UserResponse from(MemberSummary summary) {
    return new UserResponse(
        summary.id(),
        summary.name(),
        summary.generation(),
        summary.profileImage(),
        summary.hasProfile(),
        summary.editActivitiesAble());
  }
}
