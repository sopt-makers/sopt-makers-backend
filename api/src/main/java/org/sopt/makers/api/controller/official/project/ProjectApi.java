package org.sopt.makers.api.controller.official.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.project.service.ProjectService.SortBy;
import org.sopt.makers.domain.official.project.service.ProjectService.SortDirection;
import org.sopt.makers.domain.official.project.type.ProjectCategory;
import org.sopt.makers.domain.official.project.type.ProjectServiceType;
import org.springframework.http.ResponseEntity;

@Tag(name = "프로젝트", description = "공식 홈페이지 프로젝트 API")
public interface ProjectApi {

  @Operation(summary = "프로젝트 목록 조회")
  ResponseEntity<BaseResponse<?>> getProjects(
      ProjectCategory filter,
      ProjectServiceType platform,
      Integer generation,
      Boolean isAvailable,
      Boolean isFounding,
      SortBy sortBy,
      SortDirection sortDirection,
      @Min(1) int pageNo,
      @Min(1) int limit);

  @Operation(summary = "프로젝트 상세 조회")
  ResponseEntity<BaseResponse<?>> getProjectDetail(Long projectId);
}
