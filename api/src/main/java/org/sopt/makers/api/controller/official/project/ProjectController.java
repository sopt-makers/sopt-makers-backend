package org.sopt.makers.api.controller.official.project;

import static org.sopt.makers.api.controller.official.project.ProjectSuccessCode.GET_PROJECTS;
import static org.sopt.makers.api.controller.official.project.ProjectSuccessCode.GET_PROJECT_DETAIL;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.official.project.dto.ProjectResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.project.ProjectDetail;
import org.sopt.makers.domain.official.project.ProjectSearchCondition;
import org.sopt.makers.domain.official.project.service.ProjectService;
import org.sopt.makers.domain.official.project.type.ProjectCategory;
import org.sopt.makers.domain.official.project.type.ProjectServiceType;
import org.sopt.makers.domain.official.project.type.ProjectSortDirection;
import org.sopt.makers.domain.official.project.type.ProjectSortType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/official/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController implements ProjectApi {

  private final ProjectService projectService;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getProjects(
      @RequestParam(required = false) ProjectCategory filter,
      @RequestParam(required = false) ProjectServiceType platform,
      @RequestParam(required = false) Integer generation,
      @RequestParam(required = false) Boolean isAvailable,
      @RequestParam(required = false) Boolean isFounding,
      @RequestParam(defaultValue = "GENERATION") ProjectSortType sortBy,
      @RequestParam(defaultValue = "DESC") ProjectSortDirection sortDirection,
      @Min(1) @RequestParam(defaultValue = "1") int pageNo,
      @Min(1) @Max(100) @RequestParam(defaultValue = "10") int limit) {
    ProjectSearchCondition condition =
        new ProjectSearchCondition(
            filter, platform, generation, isAvailable, isFounding, sortBy, sortDirection);
    ProjectService.ProjectsResult result = projectService.getProjects(condition, pageNo, limit);
    return ResponseFactory.success(
        GET_PROJECTS, ProjectResponse.ProjectList.of(result, limit, pageNo));
  }

  @Override
  @GetMapping("/{projectId}")
  public ResponseEntity<BaseResponse<?>> getProjectDetail(@PathVariable Long projectId) {
    ProjectDetail detail = projectService.getProjectDetail(projectId);
    return ResponseFactory.success(
        GET_PROJECT_DETAIL, ProjectResponse.ProjectDetailItem.of(detail));
  }
}
