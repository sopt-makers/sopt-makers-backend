package org.sopt.makers.domain.official.project.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.ProjectDetail;
import org.sopt.makers.domain.official.project.ProjectSearchCondition;
import org.sopt.makers.domain.official.project.port.ProjectClientPort;
import org.sopt.makers.domain.official.project.type.ProjectCategory;
import org.sopt.makers.domain.official.project.type.ProjectServiceType;
import org.sopt.makers.domain.official.project.type.ProjectSortDirection;
import org.sopt.makers.domain.official.project.type.ProjectSortType;

class ProjectServiceTest {

  private final List<Project> projects =
      List.of(
          project(1L, "Z", 39, true, true),
          project(2L, "B", 39, true, true),
          project(3L, "A", 39, true, true),
          project(4L, "C", 38, true, true),
          project(5L, "D", 39, false, true),
          project(6L, "E", 39, true, false),
          project(7L, null, null, false, false),
          new Project(
              8L,
              "F",
              37,
              ProjectCategory.SOPKATHON,
              List.of(ProjectServiceType.WEB),
              null,
              null,
              null,
              false,
              false,
              List.of()));

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
  @DisplayName("필터와 정렬을 적용한 뒤 페이지를 나누고 필터링된 전체 건수를 반환한다")
  void filtersAndSortsBeforePagination() {
    var condition =
        new ProjectSearchCondition(
            null, null, 39, true, true, ProjectSortType.NAME, ProjectSortDirection.ASC);

    var result = service.getProjects(condition, 2, 2);

    assertThat(result.total()).isEqualTo(3);
    assertThat(result.projects()).extracting(Project::id).containsExactly(1L);
  }

  @Test
  @DisplayName("카테고리와 서비스 형태를 함께 필터링한다")
  void filtersByCategoryAndServiceType() {
    var condition =
        new ProjectSearchCondition(
            ProjectCategory.SOPKATHON,
            ProjectServiceType.WEB,
            null,
            null,
            null,
            ProjectSortType.GENERATION,
            ProjectSortDirection.DESC);

    assertThat(service.getProjects(condition, 1, 10).projects())
        .extracting(Project::id)
        .containsExactly(8L);
  }

  @Test
  @DisplayName("기수 내림차순은 같은 기수의 원본 순서를 유지하고 null을 마지막에 둔다")
  void generationDescendingIsStableAndKeepsNullLast() {
    assertThat(
            service
                .getProjects(sortedBy(ProjectSortType.GENERATION, ProjectSortDirection.DESC), 1, 10)
                .projects())
        .extracting(Project::id)
        .containsExactly(1L, 2L, 3L, 5L, 6L, 4L, 8L, 7L);
  }

  @Test
  @DisplayName("기수 오름차순에서도 null을 마지막에 둔다")
  void generationAscendingKeepsNullLast() {
    assertThat(
            service
                .getProjects(sortedBy(ProjectSortType.GENERATION, ProjectSortDirection.ASC), 1, 10)
                .projects())
        .extracting(Project::id)
        .containsExactly(8L, 4L, 1L, 2L, 3L, 5L, 6L, 7L);
  }

  @Test
  @DisplayName("이름 내림차순에서도 null을 마지막에 둔다")
  void nameDescendingKeepsNullLast() {
    assertThat(
            service
                .getProjects(sortedBy(ProjectSortType.NAME, ProjectSortDirection.DESC), 1, 10)
                .projects())
        .extracting(Project::id)
        .containsExactly(1L, 8L, 6L, 5L, 4L, 2L, 3L, 7L);
  }

  @Test
  @DisplayName("데이터 범위를 넘는 페이지는 오버플로 없이 빈 목록과 전체 건수를 반환한다")
  void pageBeyondDataIsEmpty() {
    var result =
        service.getProjects(
            sortedBy(ProjectSortType.NAME, ProjectSortDirection.ASC),
            Integer.MAX_VALUE,
            Integer.MAX_VALUE);

    assertThat(result.projects()).isEmpty();
    assertThat(result.total()).isEqualTo(8);
  }

  @Test
  @DisplayName("isAvailable 필터는 요청한 값과 같은 프로젝트만 반환한다")
  void filtersByIsAvailable() {
    assertThat(service.getProjects(availability(false), 1, 10).projects())
        .extracting(Project::id)
        .containsExactly(5L, 8L, 7L);
    assertThat(service.getProjects(availability(true), 1, 10).projects())
        .extracting(Project::id)
        .containsExactly(1L, 2L, 3L, 6L, 4L);
  }

  @Test
  @DisplayName("isFounding 필터는 요청한 값과 같은 프로젝트만 반환한다")
  void filtersByIsFounding() {
    var condition =
        new ProjectSearchCondition(
            null, null, null, null, false, ProjectSortType.GENERATION, ProjectSortDirection.DESC);

    var result = service.getProjects(condition, 1, 10);

    assertThat(result.total()).isEqualTo(3);
    assertThat(result.projects()).extracting(Project::id).containsExactly(6L, 8L, 7L);
  }

  private static ProjectSearchCondition sortedBy(
      ProjectSortType sortType, ProjectSortDirection direction) {
    return new ProjectSearchCondition(null, null, null, null, null, sortType, direction);
  }

  private static ProjectSearchCondition availability(boolean isAvailable) {
    return new ProjectSearchCondition(
        null, null, null, isAvailable, null, ProjectSortType.GENERATION, ProjectSortDirection.DESC);
  }

  private static Project project(
      Long id, String name, Integer generation, boolean available, boolean founding) {
    return new Project(
        id,
        name,
        generation,
        ProjectCategory.APPJAM,
        List.of(),
        null,
        null,
        null,
        founding,
        available,
        List.of());
  }
}
