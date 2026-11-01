package org.sopt.makers.domain.official.project.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.ProjectDetail;
import org.sopt.makers.domain.official.project.port.ProjectClientPort;
import org.sopt.makers.domain.official.project.service.ProjectService.SortBy;
import org.sopt.makers.domain.official.project.service.ProjectService.SortDirection;

class ProjectServiceTest {
  private final List<Project> projects =
      List.of(
          project(1L, "Z", 39, true, true),
          project(2L, "B", 39, true, true),
          project(3L, "A", 39, true, true),
          project(4L, "C", 38, true, true),
          project(5L, "D", 39, null, true),
          project(6L, "E", 39, true, null),
          project(7L, null, null, false, false));

  private final ProjectService service =
      new ProjectService(
          new ProjectClientPort() {
            public List<Project> fetchAll() {
              return projects;
            }

            public ProjectDetail fetchDetail(Long id) {
              throw new UnsupportedOperationException();
            }
          });

  @Test
  void filtersAndSortsBeforePaginationAndReturnsFilteredTotal() {
    var result =
        service.getProjects(null, null, 39, true, true, SortBy.NAME, SortDirection.ASC, 2, 2);
    assertThat(result.total()).isEqualTo(3);
    assertThat(result.projects()).extracting(Project::id).containsExactly(1L);
  }

  @Test
  void defaultOrderIsStableAndNullGenerationIsLast() {
    var result =
        service.getProjects(
            null, null, null, null, null, SortBy.GENERATION, SortDirection.DESC, 1, 10);
    assertThat(result.projects())
        .extracting(Project::id)
        .containsExactly(1L, 2L, 3L, 5L, 6L, 4L, 7L);
  }

  @Test
  void descendingNameKeepsNullLastAndLargePageIsEmpty() {
    var result =
        service.getProjects(null, null, null, null, null, SortBy.NAME, SortDirection.DESC, 1, 10);
    assertThat(result.projects())
        .extracting(Project::id)
        .containsExactly(1L, 6L, 5L, 4L, 2L, 3L, 7L);
    assertThat(
            service
                .getProjects(
                    null,
                    null,
                    null,
                    null,
                    null,
                    SortBy.NAME,
                    SortDirection.ASC,
                    Integer.MAX_VALUE,
                    Integer.MAX_VALUE)
                .projects())
        .isEmpty();
  }

  @Test
  void foundingFalseIncludesNullToMatchResponse() {
    var result =
        service.getProjects(
            null, null, null, null, false, SortBy.GENERATION, SortDirection.DESC, 1, 10);
    assertThat(result.total()).isEqualTo(2);
    assertThat(result.projects()).extracting(Project::id).containsExactly(6L, 7L);
  }

  private static Project project(
      Long id, String name, Integer generation, Boolean available, Boolean founding) {
    return new Project(
        id, name, generation, null, List.of(), null, null, null, founding, available, List.of());
  }
}
