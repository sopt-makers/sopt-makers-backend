package org.sopt.makers.clients.notification;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotification;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandNotificationSenderPort;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetingDemandNotificationSenderAdapter implements MeetingDemandNotificationSenderPort {

  private static final String SEND_ACTION = "send";
  private static final String SERVICE_NAME = "crew";
  private static final String DEV_WEB_URL = "https://sopt-internal-dev.sopt.org/group";
  private static final String PROD_WEB_URL = "https://playground.sopt.org/group";

  private final NotificationHttpClient notificationHttpClient;
  private final Environment environment;

  @Override
  public void send(MeetingDemandNotification notification) {
    notificationHttpClient.send(SERVICE_NAME, SEND_ACTION, buildBody(notification));
  }

  private Map<String, Object> buildBody(MeetingDemandNotification notification) {
    Map<String, Object> body = new HashMap<>();
    body.put("userIds", notification.userIds().stream().map(String::valueOf).toList());
    body.put("title", notification.title());
    body.put("content", notification.content());
    body.put("category", notification.category());
    body.put("webLink", getWebUrl() + notification.webPath());
    return body;
  }

  private String getWebUrl() {
    return environment.matchesProfiles("prod") ? PROD_WEB_URL : DEV_WEB_URL;
  }
}
