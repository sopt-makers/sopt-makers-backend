package org.sopt.makers.api.controller.app.notification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.sopt.makers.domain.app.notification.service.AppNotificationService;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

class AppNotificationControllerTest {

  private static final Long USER_ID = 1L;
  private static final String READ_RESPONSE =
      """
      {"success": true, "message": "알림 읽음 처리에 성공했습니다.", "data": null}
      """;

  private final AppNotificationService appNotificationService = mock(AppNotificationService.class);
  private final MockMvc mockMvc =
      AppChannelMockMvc.of(new AppNotificationController(appNotificationService), USER_ID);

  @ParameterizedTest
  @ValueSource(strings = {"/api/v2/notification/read/abc-1", "/api/v2/notification/abc-1"})
  void 알림_단건_읽음_처리(String path) throws Exception {
    mockMvc
        .perform(patch(path))
        .andExpect(status().isOk())
        .andExpect(content().json(READ_RESPONSE, JsonCompareMode.STRICT));
    verify(appNotificationService).markAsRead(USER_ID, "abc-1");
  }

  @ParameterizedTest
  @ValueSource(strings = {"/api/v2/notification/read", "/api/v2/notification"})
  void 알림_전체_읽음_처리(String path) throws Exception {
    mockMvc
        .perform(patch(path))
        .andExpect(status().isOk())
        .andExpect(content().json(READ_RESPONSE, JsonCompareMode.STRICT));
    verify(appNotificationService).markAsRead(USER_ID, null);
  }
}
