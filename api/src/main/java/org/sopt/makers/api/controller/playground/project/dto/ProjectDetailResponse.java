package org.sopt.makers.api.controller.playground.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.playground.project.service.ProjectService.ProjectDetailResult;

public record ProjectDetailResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Long id,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Long writerId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Integer generation,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String category,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) LocalDate startAt,
    LocalDate endAt,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<String> serviceType,
    Boolean isAvailable,
    Boolean isFounding,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String summary,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String detail,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String logoImage,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String thumbnailImage,
    List<String> images,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<ProjectDetailMemberResponse> members,
    List<ProjectLinkResponse> links) {
  public static ProjectDetailResponse from(ProjectDetailResult result) {
    return new ProjectDetailResponse(
        result.project().id(),
        result.project().name(),
        result.project().writerId(),
        result.project().generation(),
        result.project().category(),
        result.project().startAt(),
        result.project().endAt(),
        result.project().serviceType(),
        result.project().isAvailable(),
        result.project().isFounding(),
        result.project().summary(),
        result.project().detail(),
        result.project().logoImage(),
        result.project().thumbnailImage(),
        result.project().images(),
        result.project().createdAt(),
        result.project().updatedAt(),
        result.members().stream().map(ProjectDetailMemberResponse::from).toList(),
        result.links().stream().map(ProjectLinkResponse::from).toList());
  }
}
