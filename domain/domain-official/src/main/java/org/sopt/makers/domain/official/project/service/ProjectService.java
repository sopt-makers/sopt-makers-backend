package org.sopt.makers.domain.official.project.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.ProjectDetail;
import org.sopt.makers.domain.official.project.port.ProjectClientPort;
import org.sopt.makers.domain.official.project.type.ProjectCategory;
import org.sopt.makers.domain.official.project.type.ProjectServiceType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

  private final ProjectClientPort projectClientPort;

  public ProjectsResult getProjects(
      ProjectCategory category, ProjectServiceType serviceType, int pageNo, int limit) {
    List<Project> all = projectClientPort.fetchAll();
    List<Project> filtered =
        all.stream()
            .filter(p -> category == null || Objects.equals(p.category(), category))
            .filter(p -> serviceType == null || p.serviceType().contains(serviceType))
            .sorted(
                Comparator.comparing(
                    Project::generation, Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();
    int total = filtered.size();
    int fromIndex = (pageNo - 1) * limit;
    if (fromIndex >= total) {
      return new ProjectsResult(List.of(), total);
    }
    int toIndex = Math.min(fromIndex + limit, total);
    return new ProjectsResult(filtered.subList(fromIndex, toIndex), total);
  }

  public ProjectDetail getProjectDetail(Long projectId) {
    return projectClientPort.fetchDetail(projectId);
  }

  public record ProjectsResult(List<Project> projects, int total) {}
}
