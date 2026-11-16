package org.sopt.makers.api.controller.official.project;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.ProjectDetail;
import org.sopt.makers.domain.official.project.ProjectSearchCondition;
import org.sopt.makers.domain.official.project.service.ProjectService;
import org.sopt.makers.domain.official.project.type.ProjectLinkType;
import org.sopt.makers.domain.official.project.type.ProjectMemberRole;
import org.sopt.makers.domain.official.project.type.ProjectSortDirection;
import org.sopt.makers.domain.official.project.type.ProjectSortType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

class ProjectControllerTest {

  private static final String PROJECTS_URL = "/v2/projects";
  private static final String TYPE_MISMATCH_RESPONSE =
      """
      {"success": false, "message": "입력한 값의 타입이 잘못되었습니다", "data": null}
      """;

  private final ProjectService service = mock(ProjectService.class);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(service.getProjects(any(), anyInt(), anyInt()))
        .thenReturn(new ProjectService.ProjectsResult(List.of(), 0));
    mvc =
        MockMvcBuilders.standaloneSetup(validatedController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  @DisplayName("조회 옵션을 검색 조건으로 바인딩한다")
  void bindsQueryOptions() throws Exception {
    mvc.perform(
            get(PROJECTS_URL)
                .param("generation", "39")
                .param("isAvailable", "true")
                .param("isFounding", "false")
                .param("sortBy", "NAME")
                .param("sortDirection", "ASC")
                .param("pageNo", "2")
                .param("limit", "5"))
        .andExpect(status().isOk());

    verify(service)
        .getProjects(
            new ProjectSearchCondition(
                null, null, 39, true, false, ProjectSortType.NAME, ProjectSortDirection.ASC),
            2,
            5);
  }

  @Test
  @DisplayName("옵션이 없으면 기수 내림차순 첫 페이지를 조회한다")
  void appliesDefaultOptions() throws Exception {
    mvc.perform(get(PROJECTS_URL)).andExpect(status().isOk());

    verify(service)
        .getProjects(
            new ProjectSearchCondition(
                null,
                null,
                null,
                null,
                null,
                ProjectSortType.GENERATION,
                ProjectSortDirection.DESC),
            1,
            10);
  }

  @Test
  @DisplayName("목록 응답에 이용 가능 여부와 창업 여부를 포함한다")
  void listResponseIncludesServiceFlags() throws Exception {
    Project project =
        new Project(
            1L, "프로젝트", 39, null, List.of(), null, null, null, null, false, true, List.of());
    when(service.getProjects(any(), anyInt(), anyInt()))
        .thenReturn(new ProjectService.ProjectsResult(List.of(project), 1));

    mvc.perform(get(PROJECTS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data[0].isAvailable").value(true))
        .andExpect(jsonPath("$.data.data[0].isFounding").value(false));
  }

  @Test
  @DisplayName("목록 응답에 전체 건수와 요청한 페이지 정보를 포함한다")
  void listResponseIncludesPagination() throws Exception {
    when(service.getProjects(any(), anyInt(), anyInt()))
        .thenReturn(new ProjectService.ProjectsResult(List.of(), 25));

    mvc.perform(get(PROJECTS_URL).param("pageNo", "3").param("limit", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data").isArray())
        .andExpect(jsonPath("$.data.totalCount").value(25))
        .andExpect(jsonPath("$.data.limit").value(5))
        .andExpect(jsonPath("$.data.pageNo").value(3));
  }

  @Test
  @DisplayName("상세 응답에 이용 가능 여부와 창업 여부를 포함한다")
  void detailResponseIncludesServiceFlags() throws Exception {
    ProjectDetail detail =
        new ProjectDetail(
            1L, "프로젝트", 39, null, List.of(), null, null, null, null, null, false, true, null, null,
            null, null, List.of(), List.of());
    when(service.getProjectDetail(1L)).thenReturn(detail);

    mvc.perform(get(PROJECTS_URL + "/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.isAvailable").value(true))
        .andExpect(jsonPath("$.data.isFounding").value(false));
  }

  @Test
  @DisplayName("링크는 FE가 사용하는 title·url 형태와 레거시 값으로 응답한다")
  void linksUseLegacyShape() throws Exception {
    Project project =
        new Project(
            1L,
            "프로젝트",
            39,
            null,
            List.of(),
            null,
            null,
            null,
            null,
            false,
            false,
            List.of(
                new Project.ProjectLink(ProjectLinkType.GOOGLE_PLAYSTORE, "https://play"),
                new Project.ProjectLink(ProjectLinkType.APP_STORE, "https://apps")));
    when(service.getProjects(any(), anyInt(), anyInt()))
        .thenReturn(new ProjectService.ProjectsResult(List.of(project), 1));

    mvc.perform(get(PROJECTS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data[0].links[0].title").value("googlePlay"))
        .andExpect(jsonPath("$.data.data[0].links[0].url").value("https://play"))
        .andExpect(jsonPath("$.data.data[0].links[1].title").value("appStore"))
        .andExpect(jsonPath("$.data.data[0].links[0].linkType").doesNotExist())
        .andExpect(jsonPath("$.data.data[0].links[0].linkUrl").doesNotExist());
  }

  @Test
  @DisplayName("상세 응답의 링크는 레거시 값, 멤버 역할은 표시용 역할명으로 응답한다")
  void detailUsesLegacyLinkAndRoleValues() throws Exception {
    ProjectDetail detail =
        new ProjectDetail(
            1L,
            "프로젝트",
            39,
            null,
            List.of(),
            null,
            null,
            null,
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            null,
            List.of(new ProjectDetail.ProjectLink(ProjectLinkType.WEBSITE, "https://web")),
            List.of(
                new ProjectDetail.ProjectMember("김솝트", ProjectMemberRole.DESIGN, "UI 디자인"),
                new ProjectDetail.ProjectMember("이솝트", ProjectMemberRole.WEB, null),
                new ProjectDetail.ProjectMember("박솝트", null, null)));
    when(service.getProjectDetail(1L)).thenReturn(detail);

    mvc.perform(get(PROJECTS_URL + "/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.links[0].title").value("website"))
        .andExpect(jsonPath("$.data.links[0].url").value("https://web"))
        .andExpect(jsonPath("$.data.members[0].role").value("디자이너"))
        .andExpect(jsonPath("$.data.members[0].description").value("UI 디자인"))
        .andExpect(jsonPath("$.data.members[1].role").value("웹 프론트엔드 개발자"))
        .andExpect(jsonPath("$.data.members[2].role").value(nullValue()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"sortBy", "sortDirection", "generation", "isAvailable", "pageNo"})
  @DisplayName("타입이 잘못된 조회 파라미터는 내부 정보 없이 400을 반환한다")
  void rejectsTypeMismatch(String parameter) throws Exception {
    mvc.perform(get(PROJECTS_URL).param(parameter, "invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(content().json(TYPE_MISMATCH_RESPONSE, JsonCompareMode.STRICT));
  }

  @ParameterizedTest
  @ValueSource(strings = {"pageNo", "limit"})
  @DisplayName("1보다 작은 페이지 파라미터는 400과 필드명을 반환한다")
  void rejectsValueBelowMinimum(String parameter) throws Exception {
    mvc.perform(get(PROJECTS_URL).param(parameter, "0"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.data.valid_" + parameter).exists());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("페이지 크기는 100까지 허용한다")
  void allowsMaximumPageSize() throws Exception {
    mvc.perform(get(PROJECTS_URL).param("limit", "100")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("페이지 크기가 100을 초과하면 400과 필드명을 반환한다")
  void rejectsPageSizeAboveMaximum() throws Exception {
    mvc.perform(get(PROJECTS_URL).param("limit", "101"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.data.valid_limit").exists());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("프로젝트 목록에 상세 설명과 카테고리를 포함한다")
  void listIncludesDetailsAndCategory() throws Exception {
    Project project =
        new Project(
            1L,
            "프로젝트",
            39,
            org.sopt.makers.domain.official.project.type.ProjectCategory.APPJAM,
            List.of(),
            "소개",
            "상세 설명",
            "logo",
            "thumbnail",
            false,
            true,
            List.of(
                new Project.ProjectLink(
                    org.sopt.makers.domain.official.project.type.ProjectLinkType.WEBSITE,
                    "https://sopt.org")));
    when(service.getProjects(any(), anyInt(), anyInt()))
        .thenReturn(new ProjectService.ProjectsResult(List.of(project), 25));
    mvc.perform(get(PROJECTS_URL).param("pageNo", "2").param("limit", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data[0].detail").value("상세 설명"))
        .andExpect(jsonPath("$.data.data[0].category").value("APPJAM"));
  }

  @Test
  @DisplayName("프로젝트 상세의 등록 일시는 밀리초와 Z를 포함하고 빈 이미지는 제외한다")
  void detailFormatsDates() throws Exception {
    var time = java.time.LocalDateTime.of(2026, 1, 2, 3, 4, 5, 123000000);
    when(service.getProjectDetail(1L))
        .thenReturn(
            new ProjectDetail(
                1L,
                "프로젝트",
                39,
                null,
                List.of(),
                null,
                null,
                null,
                null,
                "",
                false,
                true,
                null,
                null,
                time,
                time,
                List.of(),
                List.of(
                    new ProjectDetail.ProjectMember(
                        "김솝트",
                        org.sopt.makers.domain.official.project.type.ProjectMemberRole.SERVER,
                        "서버"))));
    mvc.perform(get(PROJECTS_URL + "/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.uploadedAt").value("2026-01-02T03:04:05.123Z"))
        .andExpect(jsonPath("$.data.updatedAt").value("2026-01-02T03:04:05.123Z"))
        .andExpect(jsonPath("$.data.projectImage").doesNotExist());
  }

  private Object validatedController() {
    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();
    MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
    processor.setValidator(validator.getValidator());
    processor.setProxyTargetClass(true);
    processor.afterPropertiesSet();
    return processor.postProcessAfterInitialization(
        new ProjectController(service), "projectController");
  }
}
