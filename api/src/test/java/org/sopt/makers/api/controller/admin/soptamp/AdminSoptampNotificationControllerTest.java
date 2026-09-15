package org.sopt.makers.api.controller.admin.soptamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.sopt.makers.domain.app.soptamp.facade.AdminSoptampFacade;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

class AdminSoptampNotificationControllerTest {

  private static final String BODY =
      """
      {"nickname": "서버홍길동", "missionId": 10, "notificationTitle": "제목", "notificationContent": "본문"}
      """;

  private final AdminSoptampFacade adminSoptampFacade = mock(AdminSoptampFacade.class);
  private final MockMvc mockMvc =
      AppChannelMockMvc.ofAnonymous(
          new AdminSoptampNotificationController(
              adminSoptampFacade, new AdminSoptampNotificationApiKeyVerifier("secret")));

  @Test
  void 키가_맞으면_발송하고_200() throws Exception {
    mockMvc
        .perform(
            post("/api/v2/admin/notification/soptamp/showcase")
                .header("apiKey", "secret")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {"success": true, "message": "솝탬프 쇼케이스 알림 발송이 완료되었습니다", "data": null}
                    """,
                    JsonCompareMode.STRICT));
    verify(adminSoptampFacade).sendSoptampShowcase(10L, "서버홍길동", "제목", "본문");
  }

  @Test
  void 키가_틀리면_401_이고_발송하지_않는다() throws Exception {
    mockMvc
        .perform(
            post("/api/v2/admin/notification/soptamp/showcase")
                .header("apiKey", "wrong")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isUnauthorized())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "잘못된 솝탬프 알림 API 키입니다.", "data": null}
                    """,
                    JsonCompareMode.STRICT));
    verify(adminSoptampFacade, never()).sendSoptampShowcase(anyLong(), any(), any(), any());
  }

  @Test
  void 제목이_비면_400_이고_발송하지_않는다() throws Exception {
    mockMvc
        .perform(
            post("/api/v2/admin/notification/soptamp/showcase")
                .header("apiKey", "secret")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"nickname": "서버홍길동", "missionId": 10, "notificationTitle": "", "notificationContent": "본문"}
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
                    {"success": false, "message": "유효하지 않은 입력 값입니다", "data": {"valid_notificationTitle": "알림 제목은 필수입니다"}}
                    """,
                    JsonCompareMode.STRICT));
    verify(adminSoptampFacade, never()).sendSoptampShowcase(anyLong(), any(), any(), any());
  }
}
