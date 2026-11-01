package org.sopt.makers.api.controller.official.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
import org.sopt.makers.domain.official.project.type.ProjectSortDirection;
import org.sopt.makers.domain.official.project.type.ProjectSortType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

class ProjectControllerTest {

  private static final String PROJECTS_URL = "/api/v1/official/projects";
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
        new Project(1L, "프로젝트", 39, null, List.of(), null, null, null, false, true, List.of());
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
  }

  @Test
  @DisplayName("limit은 100까지 허용하고 초과하면 400과 필드명을 반환한다")
  void limitIsBoundedByOneHundred() throws Exception {
    mvc.perform(get(PROJECTS_URL).param("limit", "100")).andExpect(status().isOk());
    mvc.perform(get(PROJECTS_URL).param("limit", "101"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.data.valid_limit").exists());
  }

  /** 운영과 같은 후처리기를 적용해 {@code @Validated}가 빠지면 검증 테스트가 실패하도록 한다. */
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
