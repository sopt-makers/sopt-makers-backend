package org.sopt.makers.api.controller.official.project;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.domain.official.project.service.ProjectService;
import org.sopt.makers.domain.official.project.service.ProjectService.SortBy;
import org.sopt.makers.domain.official.project.service.ProjectService.SortDirection;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;

class ProjectControllerTest {
  private final ProjectService service = mock(ProjectService.class);
  private final org.springframework.test.web.servlet.MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new ProjectController(service))
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @Test
  void bindsQueryOptions() throws Exception {
    when(service.getProjects(any(), any(), any(), any(), any(), any(), any(), eq(1), eq(5)))
        .thenReturn(new ProjectService.ProjectsResult(List.of(), 0));
    mvc.perform(
            get("/api/v1/official/projects")
                .param("generation", "39")
                .param("isAvailable", "true")
                .param("isFounding", "false")
                .param("sortBy", "NAME")
                .param("sortDirection", "ASC")
                .param("limit", "5"))
        .andExpect(status().isOk());
    verify(service).getProjects(null, null, 39, true, false, SortBy.NAME, SortDirection.ASC, 1, 5);
  }

  @Test
  void invalidQueryReportsFieldWithoutInternalType() throws Exception {
    mvc.perform(get("/api/v1/official/projects").param("sortBy", "invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.data.valid_sortBy").value("유효하지 않은 입력값입니다"));
  }

  @Test
  void methodValidationAcceptsValidValuesAndRejectsInvalidPageAndLimit() {
    try (LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean()) {
      validator.afterPropertiesSet();
      ProxyFactory factory = new ProxyFactory(new ProjectController(service));
      factory.setProxyTargetClass(true);
      factory.addAdvice(new MethodValidationInterceptor(validator.getValidator()));
      ProjectController controller = (ProjectController) factory.getProxy();
      when(service.getProjects(any(), any(), any(), any(), any(), any(), any(), eq(1), eq(10)))
          .thenReturn(new ProjectService.ProjectsResult(List.of(), 0));
      controller.getProjects(
          null, null, null, null, null, SortBy.GENERATION, SortDirection.DESC, 1, 10);
      assertThatThrownBy(
              () ->
                  controller.getProjects(
                      null, null, null, null, null, SortBy.GENERATION, SortDirection.DESC, 0, 10))
          .isInstanceOf(ConstraintViolationException.class);
      assertThatThrownBy(
              () ->
                  controller.getProjects(
                      null, null, null, null, null, SortBy.GENERATION, SortDirection.DESC, 1, 0))
          .isInstanceOf(ConstraintViolationException.class);
    }
  }
}
