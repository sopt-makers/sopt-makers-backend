package org.sopt.makers.domain.official.project.service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.ProjectDetail;
import org.sopt.makers.domain.official.project.ProjectSearchCondition;
import org.sopt.makers.domain.official.project.port.ProjectClientPort;
import org.sopt.makers.domain.official.project.type.ProjectSortDirection;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

  private final ProjectClientPort projectClientPort;

  public ProjectsResult getProjects(ProjectSearchCondition condition, int pageNo, int limit) {
    List<Project> filtered =
        projectClientPort.fetchAll().stream()
            .filter(project -> matches(project, condition))
            .sorted(comparatorOf(condition))
            .toList();
    long offset = (long) (pageNo - 1) * limit;
    return new ProjectsResult(
        filtered.stream().skip(offset).limit(limit).toList(), filtered.size());
  }

  public ProjectDetail getProjectDetail(Long projectId) {
    return projectClientPort.fetchDetail(projectId);
  }

  private boolean matches(Project project, ProjectSearchCondition condition) {
    return (condition.category() == null || condition.category() == project.category())
        && (condition.serviceType() == null
            || project.serviceType().contains(condition.serviceType()))
        && (condition.generation() == null || condition.generation().equals(project.generation()))
        && (condition.isAvailable() == null || condition.isAvailable() == project.isAvailable())
        && (condition.isFounding() == null || condition.isFounding() == project.isFounding());
  }

  private Comparator<Project> comparatorOf(ProjectSearchCondition condition) {
    ProjectSortDirection direction = condition.sortDirection();
    return switch (condition.sortType()) {
      case GENERATION -> Comparator.comparing(Project::generation, nullsLast(direction));
      case NAME -> Comparator.comparing(Project::name, nullsLast(direction));
    };
  }

  private <T extends Comparable<? super T>> Comparator<T> nullsLast(
      ProjectSortDirection direction) {
    return Comparator.nullsLast(
        direction == ProjectSortDirection.ASC
            ? Comparator.<T>naturalOrder()
            : Comparator.<T>reverseOrder());
  }

  public record ProjectsResult(List<Project> projects, int total) {}
}
