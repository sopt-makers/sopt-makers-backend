package org.sopt.makers.api.controller.admin.soptamp;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.domain.app.soptamp.rank.service.RankService;
import org.sopt.makers.domain.app.soptamp.service.SoptampBatchService;
import org.sopt.makers.domain.app.soptamp.service.SoptampUpsertResult;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminScheduleControllerTest {

  private static final String PASSWORD = "test-admin-password";
  private static final String PASSWORD_HEADER = "x-admin-password";

  private final RankService rankService = mock(RankService.class);
  private final SoptampBatchService soptampBatchService = mock(SoptampBatchService.class);

  private final MockMvc mockMvc =
      MockMvcBuilders.standaloneSetup(
              new AdminScheduleController(
                  rankService, soptampBatchService, new AdminSoptampPasswordVerifier(PASSWORD)))
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @Test
  void 랭킹_캐시_수동_동기화_응답_모양() throws Exception {
    mockMvc
        .perform(
            post("/api/v2/admin/schedule/soptamp/sync-rank-cache")
                .header(PASSWORD_HEADER, PASSWORD))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {"success": true, "message": "솝탬프 랭킹 캐시 동기화가 완료되었습니다", "data": null}
                    """,
                    JsonCompareMode.STRICT));

    then(rankService).should().reloadRankCache();
  }

  @Test
  void upsert_배치_수동_실행_응답_모양() throws Exception {
    given(soptampBatchService.upsertAllSoptampUsers())
        .willReturn(new SoptampUpsertResult(250, 250, 0, 0, 0));

    mockMvc
        .perform(post("/api/v2/admin/schedule/soptamp/upsert").header(PASSWORD_HEADER, PASSWORD))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "솝탬프 유저 upsert 배치 실행이 완료되었습니다",
                      "data": {
                        "failed": false,
                        "requestedCount": 250,
                        "processedCount": 250,
                        "skippedByNullGenerationCount": 0,
                        "missingProfileCount": 0,
                        "failedChunkCount": 0
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));

    then(soptampBatchService).should().upsertAllSoptampUsers();
  }

  @Test
  void 건너뜀이나_누락이_있으면_200_이지만_실패로_표시된다() throws Exception {
    given(soptampBatchService.upsertAllSoptampUsers())
        .willReturn(new SoptampUpsertResult(250, 246, 2, 1, 1));

    mockMvc
        .perform(post("/api/v2/admin/schedule/soptamp/upsert").header(PASSWORD_HEADER, PASSWORD))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "솝탬프 유저 upsert 배치 실행이 완료되었습니다",
                      "data": {
                        "failed": true,
                        "requestedCount": 250,
                        "processedCount": 246,
                        "skippedByNullGenerationCount": 2,
                        "missingProfileCount": 1,
                        "failedChunkCount": 1
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 비밀번호가_틀리면_401_이고_배치를_돌리지_않는다() throws Exception {
    mockMvc
        .perform(post("/api/v2/admin/schedule/soptamp/upsert").header(PASSWORD_HEADER, "wrong"))
        .andExpect(status().isUnauthorized())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "잘못된 앱 어드민 패스워드입니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));

    then(soptampBatchService).shouldHaveNoInteractions();
  }

  @Test
  void 비밀번호_헤더가_없으면_400() throws Exception {
    mockMvc
        .perform(post("/api/v2/admin/schedule/soptamp/sync-rank-cache"))
        .andExpect(status().isBadRequest());

    then(rankService).shouldHaveNoInteractions();
  }
}
