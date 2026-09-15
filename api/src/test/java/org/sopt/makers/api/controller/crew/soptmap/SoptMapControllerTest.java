package org.sopt.makers.api.controller.crew.soptmap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.crew.soptmap.service.SoptMapService;
import org.springframework.test.web.servlet.MockMvc;

class SoptMapControllerTest {

  private static final Long USER_ID = 10L;

  private final SoptMapService soptMapService = mock(SoptMapService.class);
  private final MockMvc mockMvc =
      CrewChannelMockMvc.of(new SoptMapController(soptMapService), USER_ID);

  @Test
  void 추천_토글은_장소와_인증_사용자를_전달하고_결과를_응답한다() throws Exception {
    when(soptMapService.toggleRecommend(3L, USER_ID))
        .thenReturn(new SoptMapService.ToggleRecommendResult(3L, true));

    mockMvc
        .perform(put("/api/v2/map/toggle/recommend/3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.soptMapId").value(3))
        .andExpect(jsonPath("$.data.toggleStatus").value(true));

    verify(soptMapService).toggleRecommend(3L, USER_ID);
  }
}
