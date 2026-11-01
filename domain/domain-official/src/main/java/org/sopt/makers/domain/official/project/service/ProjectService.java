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

  public enum SortBy {
    GENERATION,
    NAME
  }

  public enum SortDirection {
    ASC,
    DESC
  }

  public ProjectsResult getProjects(
      ProjectCategory category,
      ProjectServiceType serviceType,
      Integer generation,
      Boolean isAvailable,
      Boolean isFounding,
      SortBy sortBy,
      SortDirection sortDirection,
      int pageNo,
      int limit) {
    Comparator<Project> comparator =
        switch (sortBy) {
          case GENERATION ->
              Comparator.comparing(
                  Project::generation,
                  Comparator.nullsLast(
                      sortDirection == SortDirection.ASC
                          ? Comparator.<Integer>naturalOrder()
                          : Comparator.<Integer>reverseOrder()));
          case NAME ->
              Comparator.comparing(
                  Project::name,
                  Comparator.nullsLast(
                      sortDirection == SortDirection.ASC
                          ? Comparator.<String>naturalOrder()
                          : Comparator.<String>reverseOrder()));
        };
    List<Project> filtered =
        projectClientPort.fetchAll().stream()
            .filter(p -> category == null || Objects.equals(p.category(), category))
            .filter(p -> serviceType == null || p.serviceType().contains(serviceType))
            .filter(p -> generation == null || Objects.equals(p.generation(), generation))
            .filter(p -> isAvailable == null || Objects.equals(p.isAvailable(), isAvailable))
            .filter(p -> isFounding == null || Objects.equals(p.isFounding(), isFounding))
            .sorted(comparator)
            .toList();
    int total = filtered.size();
    long fromIndex = (long) (pageNo - 1) * limit;
    if (fromIndex >= total) {
      return new ProjectsResult(List.of(), total);
    }
    int from = (int) fromIndex;
    int to = (int) Math.min(fromIndex + limit, total);
    return new ProjectsResult(filtered.subList(from, to), total);
  }

  public ProjectDetail getProjectDetail(Long projectId) {
    return projectClientPort.fetchDetail(projectId);
  }

  public record ProjectsResult(List<Project> projects, int total) {}
}
