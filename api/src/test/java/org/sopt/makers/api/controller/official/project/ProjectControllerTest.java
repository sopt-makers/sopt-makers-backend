package org.sopt.makers.api.controller.official.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.official.project.service.ProjectService;
import org.sopt.makers.domain.official.project.service.ProjectService.SortBy;
import org.sopt.makers.domain.official.project.service.ProjectService.SortDirection;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ProjectControllerTest {
  private final ProjectService service = mock(ProjectService.class);
  private final org.springframework.test.web.servlet.MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new ProjectController(service)).build();

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
}
