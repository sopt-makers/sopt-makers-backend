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
}
