package org.sopt.makers.api.controller.playground.project;

import static org.sopt.makers.api.controller.playground.project.ProjectSuccessCode.CREATE_PROJECT;
import static org.sopt.makers.api.controller.playground.project.ProjectSuccessCode.DELETE_PROJECT;
import static org.sopt.makers.api.controller.playground.project.ProjectSuccessCode.GET_PROJECT;
import static org.sopt.makers.api.controller.playground.project.ProjectSuccessCode.GET_PROJECTS;
import static org.sopt.makers.api.controller.playground.project.ProjectSuccessCode.GET_RANDOM_PROJECTS;
import static org.sopt.makers.api.controller.playground.project.ProjectSuccessCode.UPDATE_PROJECT;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.common.util.InfiniteScrollUtil;
import org.sopt.makers.api.controller.playground.project.dto.ProjectAllResponse;
import org.sopt.makers.api.controller.playground.project.dto.ProjectDetailResponse;
import org.sopt.makers.api.controller.playground.project.dto.ProjectLinkResponse;
import org.sopt.makers.api.controller.playground.project.dto.ProjectResponse;
import org.sopt.makers.api.controller.playground.project.dto.ProjectSaveRequest;
import org.sopt.makers.api.controller.playground.project.dto.ProjectUpdateRequest;
import org.sopt.makers.api.controller.playground.project.dto.RandomProjectResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.playground.project.ProjectLink;
import org.sopt.makers.domain.playground.project.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("playgroundProjectController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController implements ProjectApi {

  private final ProjectService projectService;
  private final InfiniteScrollUtil infiniteScrollUtil;

  @Override
  @GetMapping("/random")
  public ResponseEntity<BaseResponse<?>> getRandomProjects() {
    List<RandomProjectResponse> responses =
        projectService.getRandomProjects().stream().map(RandomProjectResponse::from).toList();
    return ResponseFactory.success(GET_RANDOM_PROJECTS, responses);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<?>> getProject(@PathVariable Long id) {
    return ResponseFactory.success(
        GET_PROJECT, ProjectDetailResponse.from(projectService.getProjectDetail(id)));
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getProjects(
      @RequestParam(required = false) Integer limit,
      @RequestParam(required = false) Long cursor,
      @RequestParam(required = false, name = "name") String searchWord,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) Boolean isAvailable,
      @RequestParam(required = false) Boolean isFounding,
      @RequestParam(required = false) Integer generation) {
    List<Project> projectList =
        projectService.fetchAll(
            infiniteScrollUtil.checkLimitForPagination(limit),
            cursor,
            searchWord,
            category,
            isAvailable,
            isFounding,
            generation);

    Boolean hasNext = infiniteScrollUtil.checkHasNextElement(limit, projectList);
    List<Project> responseList = infiniteScrollUtil.removeNextElementIfExist(limit, projectList);

    List<Long> projectIds = responseList.stream().map(Project::id).toList();
    Map<Long, List<ProjectLinkResponse>> linksByProjectId =
        projectService.getProjectLinks(projectIds).stream()
            .collect(
                Collectors.groupingBy(
                    ProjectLink::projectId,
                    Collectors.mapping(ProjectLinkResponse::from, Collectors.toList())));

    List<ProjectResponse> projectResponses =
        responseList.stream()
            .map(
                project ->
                    ProjectResponse.from(
                        project, linksByProjectId.getOrDefault(project.id(), List.of())))
            .toList();

    int totalCount =
        projectService.getProjectsCount(searchWord, category, isAvailable, isFounding, generation);

    return ResponseFactory.success(
        GET_PROJECTS, new ProjectAllResponse(projectResponses, hasNext, totalCount));
  }

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createProject(
      @CurrentUserId Long userId, @Valid @RequestBody ProjectSaveRequest request) {
    projectService.createProject(
        request.name(),
        userId,
        request.generation(),
        request.category(),
        request.startAt(),
        request.endAt(),
        request.serviceType(),
        request.isAvailable(),
        request.isFounding(),
        request.summary(),
        request.detail(),
        request.logoImage(),
        request.thumbnailImage(),
        request.images(),
        request.toMembers(),
        request.toLinks());
    return ResponseFactory.success(CREATE_PROJECT);
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<BaseResponse<?>> updateProject(
      @PathVariable("id") Long projectId,
      @CurrentUserId Long userId,
      @Valid @RequestBody ProjectUpdateRequest request) {
    projectService.updateProject(
        userId,
        projectId,
        request.name(),
        request.generation(),
        request.category(),
        request.startAt(),
        request.endAt(),
        request.serviceType(),
        request.isAvailable(),
        request.isFounding(),
        request.summary(),
        request.detail(),
        request.logoImage(),
        request.thumbnailImage(),
        request.images(),
        request.toMembers(),
        request.toLinks());
    return ResponseFactory.success(UPDATE_PROJECT);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponse<?>> deleteProject(
      @PathVariable("id") Long projectId, @CurrentUserId Long userId) {
    projectService.deleteProject(userId, projectId);
    return ResponseFactory.success(DELETE_PROJECT);
  }
}
