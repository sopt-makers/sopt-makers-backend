package org.sopt.makers.api.controller.admin.soptletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.support.SoptLetterIntegrationTestSupport;
import org.sopt.makers.domain.app.push.PushMessage;
import org.springframework.http.MediaType;

class AdminSoptLetterNotificationControllerIntegrationTest
    extends SoptLetterIntegrationTestSupport {

  private static final String ADMIN_BASE = "/api/v2/admin/notification/sopt-letter";
  private static final String API_KEY = "test-notification-api-key";

  @Test
  @DisplayName("어드민 알림 API는 API 키가 틀리면 401을 돌려주고 보내지 않는다")
  void rejectsWrongApiKey() throws Exception {
    mvc.perform(
            post(ADMIN_BASE + "/unwritten-reminder")
                .header("apiKey", "wrong-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationBody()))
        .andExpect(status().isUnauthorized());

    verify(pushSenderPort, never()).send(any());
  }

  @Test
  @DisplayName("미작성 리마인드는 현재 기수 회원 중 솝레터를 쓰지 않은 사람에게만 보낸다")
  void unwrittenReminderSendsToUnwrittenCurrentMembers() throws Exception {
    long unwritten = user(CURRENT_GENERATION);
    long written = user(CURRENT_GENERATION);
    user(CURRENT_GENERATION - 1);
    profile(written, "익명의 달콤한 호떡", true);

    mvc.perform(
            post(ADMIN_BASE + "/unwritten-reminder")
                .header("apiKey", API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationBody()))
        .andExpect(status().isOk());

    assertThat(sentPush().userIds()).containsExactly(unwritten);
  }

  @Test
  @DisplayName("새 주제 알림은 현재 기수 회원 전체에게 보낸다")
  void newTopicSendsToAllCurrentMembers() throws Exception {
    long first = user(CURRENT_GENERATION);
    long second = user(CURRENT_GENERATION);
    user(CURRENT_GENERATION - 1);

    mvc.perform(
            post(ADMIN_BASE + "/new-topic")
                .header("apiKey", API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(notificationBody()))
        .andExpect(status().isOk());

    PushMessage push = sentPush();
    assertThat(push.userIds()).containsExactlyInAnyOrder(first, second);
    assertThat(push.title()).isEqualTo("새 주제가 열렸어요");
    assertThat(push.deepLink()).isEqualTo("soptletter");
  }

  private String notificationBody() {
    return "{\"notificationTitle\":\"새 주제가 열렸어요\","
        + "\"notificationContent\":\"앱잼 회고를 남겨 보세요\","
        + "\"deepLink\":\"soptletter\"}";
  }
}
