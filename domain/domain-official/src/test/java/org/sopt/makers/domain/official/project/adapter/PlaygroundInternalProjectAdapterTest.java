package org.sopt.makers.domain.official.project.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.ProjectDetail;
import org.sopt.makers.domain.official.project.type.ProjectLinkType;
import org.sopt.makers.domain.official.project.type.ProjectMemberRole;
import org.sopt.makers.domain.playground.project.port.OfficialProjectPort;
import org.sopt.makers.domain.playground.project.port.OfficialProjectPort.OfficialProjectLinkInfo;
import org.sopt.makers.domain.playground.project.port.OfficialProjectPort.OfficialProjectMemberInfo;

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
                      List.of(
                          new OfficialProjectLinkInfo("github", "https://github"),
                          new OfficialProjectLinkInfo("youtube", "https://youtube"),
                          new OfficialProjectLinkInfo(null, "https://null"))));
            }

            public OfficialProjectDetailInfo fetchDetail(Long projectId) {
              return new OfficialProjectDetailInfo(
                  projectId,
                  "프로젝트",
                  39,
                  "APPJAM",
                  List.of(),
                  null,
                  null,
                  null,
                  null,
                  List.of(),
                  false,
                  true,
                  null,
                  null,
                  null,
                  null,
                  List.of(),
                  List.of(
                      new OfficialProjectMemberInfo("김솝트", "디자이너", null),
                      new OfficialProjectMemberInfo("이솝트", "PLAN", null),
                      new OfficialProjectMemberInfo("박솝트", null, null)));
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

  @Test
  @DisplayName("알 수 없거나 값이 없는 링크 종류는 WEBSITE로 변환한다")
  void defaultsUnknownLinkTypeToWebsite() {
    List<ProjectLinkType> types =
        adapter.fetchAll().get(0).links().stream().map(Project.ProjectLink::linkType).toList();

    assertThat(types)
        .containsExactly(ProjectLinkType.GITHUB, ProjectLinkType.WEBSITE, ProjectLinkType.WEBSITE);
  }

  @Test
  @DisplayName("역할은 레거시처럼 역할명으로도 찾고, 알 수 없거나 값이 없으면 null로 변환한다")
  void parsesMemberRoles() {
    List<ProjectMemberRole> roles =
        adapter.fetchDetail(PROJECT_ID).members().stream()
            .map(ProjectDetail.ProjectMember::role)
            .toList();

    assertThat(roles).containsExactly(ProjectMemberRole.DESIGN, null, null);
  }
}
