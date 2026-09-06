package org.sopt.makers.clients.notification;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.post.PostNotification;
import org.sopt.makers.domain.playground.post.port.PostNotificationSenderPort;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostNotificationSenderAdapter implements PostNotificationSenderPort {

  private static final String SEND_ACTION = "send";
  private static final String SERVICE_NAME = "crew";
  private static final String DEV_WEB_URL = "https://sopt-internal-dev.sopt.org/group";
  private static final String PROD_WEB_URL = "https://playground.sopt.org/group";

  private final NotificationHttpClient notificationHttpClient;
  private final Environment environment;

  @Override
  public void send(PostNotification notification) {
    if (notification.userIds().isEmpty()) {
      return;
    }
    Map<String, Object> body = new HashMap<>();
    body.put("userIds", notification.userIds().stream().map(String::valueOf).toList());
    body.put("title", notification.title());
    body.put("content", notification.content());
    body.put("category", "NEWS");
    body.put("webLink", getWebUrl() + "/post?id=" + notification.postId());
    notificationHttpClient.send(SERVICE_NAME, SEND_ACTION, body);
  }

  private String getWebUrl() {
    return environment.matchesProfiles("prod") ? PROD_WEB_URL : DEV_WEB_URL;
  }
}
