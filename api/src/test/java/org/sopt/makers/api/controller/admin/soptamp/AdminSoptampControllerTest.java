package org.sopt.makers.api.controller.admin.soptamp;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.facade.AdminSoptampFacade;
import org.sopt.makers.domain.app.soptamp.service.SoptampBatchService;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminSoptampControllerTest {

  private static final String PASSWORD = "test-admin-password";

  private final AdminSoptampFacade adminSoptampFacade = mock(AdminSoptampFacade.class);
  private final SoptampBatchService soptampBatchService = mock(SoptampBatchService.class);

  private final MockMvc mockMvc =
      MockMvcBuilders.standaloneSetup(
              new AdminSoptampController(
                  adminSoptampFacade,
                  soptampBatchService,
                  new AdminSoptampPasswordVerifier(PASSWORD)))
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @Test
  void 솝탬프_데이터_초기화_응답_모양() throws Exception {
    mockMvc
        .perform(delete("/api/v2/admin/soptamp/clear").param("password", PASSWORD))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {"success": true, "message": "솝탬프 데이터 초기화가 완료되었습니다", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 삭제_범위를_안_주면_스탬프와_솝탬프_유저를_모두_지운다() throws Exception {
    mockMvc
        .perform(delete("/api/v2/admin/soptamp/clear").param("password", PASSWORD))
        .andExpect(status().isOk());

    then(adminSoptampFacade).should().clearSoptampData(true, true);
  }

  @Test
  void 삭제_범위를_주면_그대로_넘긴다() throws Exception {
    mockMvc
        .perform(
            delete("/api/v2/admin/soptamp/clear")
                .param("password", PASSWORD)
                .param("stamp", "false")
                .param("soptampUser", "true"))
        .andExpect(status().isOk());

    then(adminSoptampFacade).should().clearSoptampData(false, true);
  }

  @Test
  void 솝탬프_점수_초기화_응답_모양() throws Exception {
    mockMvc
        .perform(delete("/api/v2/admin/soptamp/point").param("password", PASSWORD))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {"success": true, "message": "솝탬프 점수 초기화가 완료되었습니다", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 랭킹_캐시_재적재_응답_모양() throws Exception {
    mockMvc
        .perform(delete("/api/v2/admin/soptamp/cache").param("password", PASSWORD))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {"success": true, "message": "솝탬프 랭킹 캐시 재적재가 완료되었습니다", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void upsert_배치_스케줄_변경_응답_모양() throws Exception {
    mockMvc
        .perform(
            patch("/api/v2/admin/soptamp/upsert/schedule")
                .param("password", PASSWORD)
                .param("cron", "0 0 3 * * *"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {"success": true, "message": "솝탬프 upsert 배치 스케줄 변경이 완료되었습니다", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 잘못된_cron_이면_400_이고_구서버_메시지를_그대로_담는다() throws Exception {
    willThrow(new SoptampException(SoptampFailure.INVALID_UPSERT_CRON))
        .given(soptampBatchService)
        .setUpsertCron("매일 세시");

    mockMvc
        .perform(
            patch("/api/v2/admin/soptamp/upsert/schedule")
                .param("password", PASSWORD)
                .param("cron", "매일 세시"))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "잘못된 파라미터 입니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 앱잼_시즌이면_랭킹_캐시_재적재는_400() throws Exception {
    willThrow(new SoptampException(SoptampFailure.INVALID_APPJAM_SEASON_REQUEST))
        .given(adminSoptampFacade)
        .initRankCache();

    mockMvc
        .perform(delete("/api/v2/admin/soptamp/cache").param("password", PASSWORD))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "앱잼탬프 시즌이므로 부적절한 요청입니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
  }

  @Test
  void 비밀번호가_틀리면_401_이고_아무것도_지우지_않는다() throws Exception {
    mockMvc
        .perform(delete("/api/v2/admin/soptamp/clear").param("password", "wrong"))
        .andExpect(status().isUnauthorized())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "잘못된 앱 어드민 패스워드입니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));

    then(adminSoptampFacade).shouldHaveNoInteractions();
  }

  @Test
  void 비밀번호가_틀리면_cron_을_보기_전에_401() throws Exception {
    mockMvc
        .perform(
            patch("/api/v2/admin/soptamp/upsert/schedule")
                .param("password", "wrong")
                .param("cron", "매일 세시"))
        .andExpect(status().isUnauthorized());

    then(soptampBatchService).should(never()).setUpsertCron("매일 세시");
  }

  @Test
  void 비밀번호_파라미터가_없으면_400() throws Exception {
    mockMvc.perform(delete("/api/v2/admin/soptamp/point")).andExpect(status().isBadRequest());

    then(adminSoptampFacade).shouldHaveNoInteractions();
  }
}
