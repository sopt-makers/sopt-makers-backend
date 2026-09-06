package org.sopt.makers.api.controller.playground.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.playground.member.profile.MemberSummary;

public record MemberResponse(
    @Schema(required = true) Long id,
    @Schema(required = true) String name,
    @Schema(required = true) Integer generation,
    String profileImage,
    @Schema(required = true) Boolean hasProfile,
    @Schema(required = true) Boolean editActivitiesAble) {

  public static MemberResponse from(MemberSummary summary) {
    return new MemberResponse(
        summary.id(),
        summary.name(),
        summary.generation(),
        summary.profileImage(),
        summary.hasProfile(),
        summary.editActivitiesAble());
  }
}
