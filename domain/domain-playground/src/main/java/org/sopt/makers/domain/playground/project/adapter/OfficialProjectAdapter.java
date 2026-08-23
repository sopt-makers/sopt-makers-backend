package org.sopt.makers.domain.playground.project.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.playground.project.ProjectLink;
import org.sopt.makers.domain.playground.project.port.OfficialProjectPort;
import org.sopt.makers.domain.playground.project.service.ProjectService;
import org.sopt.makers.domain.playground.project.service.ProjectService.ProjectDetailResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OfficialProjectAdapter implements OfficialProjectPort {

  private final ProjectService projectService;

  @Override
  public List<OfficialProjectInfo> fetchAll() {
    List<Project> all = projectService.fetchAllProjects();
    if (all.isEmpty()) {
      return List.of();
    }

    List<Long> projectIds = all.stream().map(Project::id).toList();
    Map<Long, List<ProjectLink>> linksByProjectId =
        projectService.getProjectLinks(projectIds).stream()
            .collect(Collectors.groupingBy(ProjectLink::projectId));

    Map<String, OfficialProjectInfo> byName = new LinkedHashMap<>();
    for (Project project : all) {
      List<OfficialProjectLinkInfo> links =
          linksByProjectId.getOrDefault(project.id(), List.of()).stream()
              .map(l -> new OfficialProjectLinkInfo(l.title(), l.url()))
              .toList();
      byName.putIfAbsent(project.name(), toOfficialProjectInfo(project, links));
    }
    return new ArrayList<>(byName.values());
  }

  @Override
  public OfficialProjectDetailInfo fetchDetail(Long projectId) {
    ProjectDetailResult result = projectService.getProjectDetail(projectId);
    Project project = result.project();

    List<OfficialProjectLinkInfo> links =
        result.links().stream().map(l -> new OfficialProjectLinkInfo(l.title(), l.url())).toList();

    List<OfficialProjectMemberInfo> members =
        result.members().stream()
            .map(m -> new OfficialProjectMemberInfo(m.name(), m.role(), m.description()))
            .toList();

    return new OfficialProjectDetailInfo(
        project.id(),
        project.name(),
        project.generation(),
        project.category(),
        project.serviceType(),
        project.summary(),
        project.detail(),
        project.logoImage(),
        project.thumbnailImage(),
        project.images(),
        project.isFounding(),
        project.startAt(),
        project.endAt(),
        project.createdAt(),
        project.updatedAt(),
        links,
        members);
  }

  private OfficialProjectInfo toOfficialProjectInfo(
      Project project, List<OfficialProjectLinkInfo> links) {
    return new OfficialProjectInfo(
        project.id(),
        project.name(),
        project.generation(),
        project.category(),
        project.serviceType(),
        project.summary(),
        project.logoImage(),
        project.thumbnailImage(),
        project.isFounding(),
        links);
  }
}
