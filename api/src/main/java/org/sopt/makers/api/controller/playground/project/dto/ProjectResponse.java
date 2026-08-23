package org.sopt.makers.api.controller.playground.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.playground.project.Project;

public record ProjectResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer generation,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String category,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> serviceType,
        Boolean isAvailable,
        Boolean isFounding,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String summary,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String detail,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String logoImage,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String thumbnailImage,
        List<ProjectLinkResponse> links
) {
    public static ProjectResponse from(Project project, List<ProjectLinkResponse> links) {
        return new ProjectResponse(
                project.id(), project.name(), project.generation(), project.category(),
                project.serviceType(), project.isAvailable(), project.isFounding(),
                project.summary(), project.detail(), project.logoImage(),
                project.thumbnailImage(), links
        );
    }
}
