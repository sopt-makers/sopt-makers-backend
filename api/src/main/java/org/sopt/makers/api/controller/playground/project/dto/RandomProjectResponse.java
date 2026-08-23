package org.sopt.makers.api.controller.playground.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import org.sopt.makers.domain.playground.project.Project;

public record RandomProjectResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer generation,
        @Schema(description = "활동 정보 (앱잼, 솝커톤 등)", requiredMode = Schema.RequiredMode.REQUIRED)
        String category,
        @Schema(description = "플랫폼 정보 (iOS, Android, Web 등)", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> serviceType,
        @Schema(description = "활동 시작일")
        LocalDate startAt,
        @Schema(description = "활동 종료일")
        LocalDate endAt,
        @Schema(description = "서비스 운영 여부")
        Boolean isAvailable,
        @Schema(description = "창업 여부")
        Boolean isFounding,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String logoImage,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String thumbnailImage
) {
    public static RandomProjectResponse from(Project project) {
        return new RandomProjectResponse(
                project.id(), project.name(), project.generation(), project.category(),
                project.serviceType(), project.startAt(), project.endAt(),
                project.isAvailable(), project.isFounding(),
                project.logoImage(), project.thumbnailImage()
        );
    }
}
