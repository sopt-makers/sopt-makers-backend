package org.sopt.makers.api.controller.playground.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import org.sopt.makers.domain.playground.project.ProjectLink;
import org.sopt.makers.domain.playground.project.ProjectMember;

public record ProjectUpdateRequest(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Integer generation,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String category,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) LocalDate startAt,
    LocalDate endAt,
    List<String> serviceType,
    Boolean isAvailable,
    Boolean isFounding,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "summary는 필수입니다.")
        @Size(max = 30, message = "summary는 30자 이하여야 합니다.")
        String summary,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "detail은 필수입니다.")
        @Size(max = 3000, message = "detail은 3000자 이하여야 합니다.")
        String detail,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String logoImage,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String thumbnailImage,
    List<String> images,
    List<ProjectMemberUpdateRequest> members,
    List<ProjectLinkUpdateRequest> links) {
  public ProjectUpdateRequest {
    if (images == null) images = List.of();
    if (members == null) members = List.of();
    if (links == null) links = List.of();
  }

  public List<ProjectMember> toMembers() {
    return members.stream()
        .map(
            m ->
                new ProjectMember(
                    null,
                    null,
                    m.memberId(),
                    m.memberRole(),
                    m.memberDescription(),
                    m.isTeamMember()))
        .toList();
  }

  public List<ProjectLink> toLinks() {
    return links.stream()
        .map(l -> new ProjectLink(null, null, l.linkTitle(), l.linkUrl()))
        .toList();
  }

  public record ProjectMemberUpdateRequest(
      Long memberId, String memberRole, String memberDescription, Boolean isTeamMember) {}

  public record ProjectLinkUpdateRequest(Long linkId, String linkTitle, String linkUrl) {}
}
