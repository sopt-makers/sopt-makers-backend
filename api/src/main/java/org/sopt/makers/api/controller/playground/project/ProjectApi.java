package org.sopt.makers.api.controller.playground.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.playground.project.dto.ProjectSaveRequest;
import org.sopt.makers.api.controller.playground.project.dto.ProjectUpdateRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "프로젝트 관련 API", description = "프로젝트 관련 API List")
@SecurityRequirement(name = "Authorization")
public interface ProjectApi {

  @Operation(summary = "랜덤 프로젝트 4개 조회")
  ResponseEntity<BaseResponse<?>> getRandomProjects();

  @Operation(summary = "프로젝트 단건 조회")
  ResponseEntity<BaseResponse<?>> getProject(@Parameter(description = "프로젝트 ID") Long id);

  @Operation(summary = "프로젝트 목록 조회", description = "cursor: 처음에는 null 또는 0, 이후 마지막으로 조회된 프로젝트 id")
  ResponseEntity<BaseResponse<?>> getProjects(
      Integer limit,
      Long cursor,
      String searchWord,
      String category,
      Boolean isAvailable,
      Boolean isFounding,
      Integer generation);

  @Operation(summary = "프로젝트 생성")
  ResponseEntity<BaseResponse<?>> createProject(ProjectSaveRequest request);

  @Operation(summary = "프로젝트 수정")
  ResponseEntity<BaseResponse<?>> updateProject(
      Long projectId, @Parameter(hidden = true) Long userId, ProjectUpdateRequest request);

  @Operation(summary = "프로젝트 삭제")
  ResponseEntity<BaseResponse<?>> deleteProject(
      Long projectId, @Parameter(hidden = true) Long userId);
}
