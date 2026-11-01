package org.sopt.makers.domain.playground.project.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.playground.project.port.OfficialProjectPort.OfficialProjectDetailInfo;
import org.sopt.makers.domain.playground.project.port.OfficialProjectPort.OfficialProjectInfo;
import org.sopt.makers.domain.playground.project.service.ProjectService;
import org.sopt.makers.domain.playground.project.service.ProjectService.ProjectDetailResult;

class OfficialProjectAdapterTest {

  private static final Long PROJECT_ID = 1L;

  private final ProjectService projectService = mock(ProjectService.class);
  private final OfficialProjectAdapter adapter = new OfficialProjectAdapter(projectService);

  @Test
  @DisplayName("목록 조회는 값이 없는 창업 여부와 이용 가능 여부를 false로 변환한다")
  void fetchAllNormalizesNullFlags() {
    when(projectService.fetchAllProjects()).thenReturn(List.of(projectWithNullFlags()));
    when(projectService.getProjectLinks(List.of(PROJECT_ID))).thenReturn(List.of());

    OfficialProjectInfo info = adapter.fetchAll().get(0);

    assertThat(info.isFounding()).isFalse();
    assertThat(info.isAvailable()).isFalse();
  }

  @Test
  @DisplayName("상세 조회는 값이 없는 창업 여부와 이용 가능 여부를 false로 변환한다")
  void fetchDetailNormalizesNullFlags() {
    when(projectService.getProjectDetail(PROJECT_ID))
        .thenReturn(new ProjectDetailResult(projectWithNullFlags(), List.of(), List.of()));

    OfficialProjectDetailInfo info = adapter.fetchDetail(PROJECT_ID);

    assertThat(info.isFounding()).isFalse();
    assertThat(info.isAvailable()).isFalse();
  }

  private static Project projectWithNullFlags() {
    return new Project(
        PROJECT_ID,
        null,
        "프로젝트",
        39,
        "APPJAM",
        null,
        null,
        List.of(),
        null,
        null,
        null,
        null,
        null,
        null,
        List.of(),
        null,
        null);
  }
}
