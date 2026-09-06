package org.sopt.makers.api.controller.app.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

class AppConfigControllerTest {

  private final MockMvc mockMvc = AppChannelMockMvc.ofAnonymous(new AppConfigController(true));

  @Test
  void 이용_가능_여부_응답_모양() throws Exception {
    mockMvc
        .perform(get("/api/v2/config/availability"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {"success": true, "message": "앱 이용 가능 여부 조회에 성공했습니다.", "data": {"isAvailable": true}}
                    """,
                    JsonCompareMode.STRICT));
  }
}
