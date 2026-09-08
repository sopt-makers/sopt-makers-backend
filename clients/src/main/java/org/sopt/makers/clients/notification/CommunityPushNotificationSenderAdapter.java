package org.sopt.makers.clients.notification;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.notification.CommunityPushNotification;
import org.sopt.makers.domain.playground.community.notification.port.CommunityPushNotificationPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityPushNotificationSenderAdapter implements CommunityPushNotificationPort {

  private static final String SERVICE_NAME = "community";
  private static final String SEND_ACTION = "send";
  private static final String CATEGORY = "NEWS";

  private final NotificationHttpClient notificationHttpClient;

  @Override
  public void send(CommunityPushNotification notification) {
    if (notification.recipientIds().isEmpty()) {
      return;
    }
    notificationHttpClient.send(SERVICE_NAME, SEND_ACTION, buildBody(notification));
  }

  private Map<String, Object> buildBody(CommunityPushNotification notification) {
    Map<String, Object> body = new HashMap<>();
    body.put(
        "userIds",
        notification.recipientIds().stream().map(String::valueOf).collect(Collectors.toList()));
    body.put("title", notification.title());
    body.put("content", notification.content());
    body.put("category", CATEGORY);
    body.put("webLink", notification.webLink());
    return body;
  }
}
