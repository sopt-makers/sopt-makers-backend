package org.sopt.makers.domain.official.project.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.ProjectDetail;
import org.sopt.makers.domain.playground.project.port.OfficialProjectPort;

class PlaygroundInternalProjectAdapterTest {

  private static final Long PROJECT_ID = 1L;

  private final PlaygroundInternalProjectAdapter adapter =
      new PlaygroundInternalProjectAdapter(
          new OfficialProjectPort() {
            public List<OfficialProjectInfo> fetchAll() {
              return List.of(
                  new OfficialProjectInfo(
                      PROJECT_ID,
                      "프로젝트",
                      39,
                      "APPJAM",
                      List.of(),
                      null,
                      "상세 설명",
                      null,
                      null,
                      false,
                      true,
                      List.of()));
            }

            public OfficialProjectDetailInfo fetchDetail(Long projectId) {
              return new OfficialProjectDetailInfo(
                  projectId, "프로젝트", 39, "APPJAM", List.of(), null, null, null, null, List.of(),
                  false, true, null, null, null, null, List.of(), List.of());
            }
          });

  @Test
  @DisplayName("목록 조회는 창업 여부와 이용 가능 여부를 각각의 필드로 변환한다")
  void fetchAllKeepsFlagsSeparate() {
    Project project = adapter.fetchAll().get(0);

    assertThat(project.detail()).isEqualTo("상세 설명");
    assertThat(project.isAvailable()).isTrue();
    assertThat(project.isFounding()).isFalse();
  }

  @Test
  @DisplayName("상세 조회는 창업 여부와 이용 가능 여부를 각각의 필드로 변환한다")
  void fetchDetailKeepsFlagsSeparate() {
    ProjectDetail detail = adapter.fetchDetail(PROJECT_ID);

    assertThat(detail.isAvailable()).isTrue();
    assertThat(detail.isFounding()).isFalse();
  }

  @Test
  @DisplayName("같은 이름의 프로젝트는 첫 번째 항목만 조회한다")
  void removesDuplicateNames() {
    var port = org.mockito.Mockito.mock(OfficialProjectPort.class);
    var first =
        new OfficialProjectPort.OfficialProjectInfo(
            1L, "프로젝트", 39, "APPJAM", List.of(), "소개", "상세", null, null, false, true, List.of());
    var second =
        new OfficialProjectPort.OfficialProjectInfo(
            2L, "프로젝트", 38, "APPJAM", List.of(), null, null, null, null, false, true, List.of());
    org.mockito.Mockito.when(port.fetchAll()).thenReturn(List.of(first, second));
    assertThat(new PlaygroundInternalProjectAdapter(port).fetchAll())
        .extracting(Project::id)
        .containsExactly(1L);
  }
}
