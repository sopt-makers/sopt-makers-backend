package org.sopt.makers.api.controller.crew.property;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.crew.property.service.CrewPropertyService;
import org.springframework.test.web.servlet.MockMvc;

class CrewPropertyControllerTest {

  private final CrewPropertyService crewPropertyService = mock(CrewPropertyService.class);
  private final MockMvc mockMvc =
      CrewChannelMockMvc.ofAnonymous(new CrewPropertyController(crewPropertyService));

  @Test
  void 키가_있으면_해당_프로퍼티만_조회한다() throws Exception {
    when(crewPropertyService.getValues("home")).thenReturn(Map.of("enabled", true));

    mockMvc
        .perform(get("/property/v2").param("key", "home"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.enabled").value(true));

    verify(crewPropertyService).getValues("home");
  }
}
