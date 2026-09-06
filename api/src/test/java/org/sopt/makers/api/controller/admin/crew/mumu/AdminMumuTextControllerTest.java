package org.sopt.makers.api.controller.admin.crew.mumu;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.domain.crew.mumu.service.MumuTextService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminMumuTextControllerTest {

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-09-07T03:00:00Z"), ZoneId.of("Asia/Seoul"));

  private final MumuTextService mumuTextService = mock(MumuTextService.class);
  private final MockMvc mockMvc =
      MockMvcBuilders.standaloneSetup(new AdminMumuTextController(mumuTextService, CLOCK))
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @Test
  void 무무_텍스트_삭제는_ID를_서비스에_전달한다() throws Exception {
    mockMvc
        .perform(delete("/api/v1/admin/crew/mumu-text/3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    verify(mumuTextService).delete(3L);
  }
}
