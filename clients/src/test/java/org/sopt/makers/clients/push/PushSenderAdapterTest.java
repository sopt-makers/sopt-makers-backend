package org.sopt.makers.clients.push;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.clients.alarm.AlarmProperty;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.PushToken;
import org.sopt.makers.domain.app.push.PushTokenPlatform;
import org.sopt.makers.domain.app.push.exception.PushException;

@DisplayName("PushSenderAdapter 테스트")
class PushSenderAdapterTest {

  private MockWebServer pushServer;
  private PushSenderAdapter adapter;

  @BeforeEach
  void setUp() throws Exception {
    pushServer = new MockWebServer();
    pushServer.start();
    String baseUrl = pushServer.url("/").toString().replaceAll("/$", "");
    adapter =
        new PushSenderAdapter(
            new AlarmProperty(baseUrl, "test-push-key", "test-arn", "operation", "app"));
  }

  @AfterEach
  void tearDown() throws Exception {
    pushServer.shutdown();
  }

  @Test
  @DisplayName("발송은 action send 헤더와 함께 대상·본문을 담아 보낸다")
  void send() throws Exception {
    enqueueSuccess();

    adapter.send(
        new PushMessage(Set.of(1L), "공지", "내용", NotificationCategory.NOTICE, "deep://link", null));

    RecordedRequest request = pushServer.takeRequest();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getHeader("action")).isEqualTo("send");
    assertThat(request.getHeader("x-api-key")).isEqualTo("test-push-key");
    assertThat(request.getHeader("service")).isEqualTo("app");
    assertThat(request.getHeader("transactionId")).isNotBlank();
    assertThat(request.getBody().readUtf8())
        .contains("\"userIds\":[\"1\"]")
        .contains("\"category\":\"NOTICE\"")
        .contains("\"deepLink\":\"deep://link\"");
  }

  @Test
  @DisplayName("값이 없는 링크는 본문에서 빠진다")
  void omitsNullLinks() throws Exception {
    enqueueSuccess();

    adapter.send(new PushMessage(Set.of(1L), "공지", "내용", NotificationCategory.NEWS, null, null));

    assertThat(pushServer.takeRequest().getBody().readUtf8())
        .doesNotContain("deepLink")
        .doesNotContain("webLink");
  }

  @Test
  @DisplayName("대상이 없으면 알림 서버를 호출하지 않는다")
  void skipsCallWhenNoTarget() {
    adapter.send(new PushMessage(Set.of(), "공지", "내용", NotificationCategory.NOTICE, null, null));

    assertThat(pushServer.getRequestCount()).isZero();
  }

  @Test
  @DisplayName("토큰 등록은 action register와 플랫폼 헤더를 함께 보낸다")
  void registerToken() throws Exception {
    enqueueSuccess();

    adapter.register(PushToken.create(1L, "device-token", PushTokenPlatform.IOS));

    RecordedRequest request = pushServer.takeRequest();
    assertThat(request.getHeader("action")).isEqualTo("register");
    assertThat(request.getHeader("platform")).isEqualTo("iOS");
    assertThat(request.getBody().readUtf8())
        .isEqualTo("{\"userIds\":[\"1\"],\"deviceToken\":\"device-token\"}");
  }

  @Test
  @DisplayName("토큰 해지는 action cancel로 보낸다")
  void deleteToken() throws Exception {
    enqueueSuccess();

    adapter.delete(PushToken.create(1L, "device-token", PushTokenPlatform.ANDROID));

    RecordedRequest request = pushServer.takeRequest();
    assertThat(request.getHeader("action")).isEqualTo("cancel");
    assertThat(request.getHeader("platform")).isEqualTo("Android");
  }

  @Test
  @DisplayName("알림 서버가 실패하면 PushException으로 바꿔 던진다")
  void translatesFailure() {
    pushServer.enqueue(new MockResponse().setResponseCode(500));

    assertThatThrownBy(
            () ->
                adapter.send(
                    new PushMessage(
                        Set.of(1L), "공지", "내용", NotificationCategory.NOTICE, null, null)))
        .isInstanceOf(PushException.class);
  }

  private void enqueueSuccess() {
    pushServer.enqueue(
        new MockResponse()
            .setBody("{\"status\":200,\"success\":true,\"message\":\"ok\"}")
            .addHeader("Content-Type", "application/json"));
  }
}
