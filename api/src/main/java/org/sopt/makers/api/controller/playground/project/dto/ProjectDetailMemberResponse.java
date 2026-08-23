package org.sopt.makers.api.controller.playground.project.dto;

import java.util.List;
import org.sopt.makers.domain.playground.project.service.ProjectService.ProjectMemberResult;

public record ProjectDetailMemberResponse(
    Long memberId,
    String memberRole,
    String memberDescription,
    Boolean isTeamMember,
    String memberName,
    List<Integer> memberGenerations,
    String memberProfileImage,
    Boolean memberHasProfile) {
  public static ProjectDetailMemberResponse from(ProjectMemberResult result) {
    return new ProjectDetailMemberResponse(
        result.userId(),
        result.role(),
        result.description(),
        result.isTeamMember(),
        result.name(),
        result.generations(),
        result.profileImage(),
        result.hasProfile());
  }
}
