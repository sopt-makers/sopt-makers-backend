package org.sopt.makers.clients.notification;

import static org.sopt.makers.domain.crew.notification.exception.MeetingNotificationFailure.FAIL_SEND_MEETING_NOTIFICATION;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.ServiceType;
import org.sopt.makers.domain.crew.notification.MeetingNotification;
import org.sopt.makers.domain.crew.notification.exception.MeetingNotificationException;
import org.sopt.makers.domain.crew.notification.port.MeetingNotificationSenderPort;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class MeetingNotificationSenderAdapter implements MeetingNotificationSenderPort {

  private static final String SEND_ACTION = "send";
  private static final String DEV_WEB_URL = "https://sopt-internal-dev.sopt.org/group";
  private static final String PROD_WEB_URL = "https://playground.sopt.org/group";

  private final NotificationHttpClient notificationHttpClient;
  private final Environment environment;

  @Override
  public void send(MeetingNotification notification) {
    try {
      notificationHttpClient.send(ServiceType.CREW, SEND_ACTION, buildBody(notification));
    } catch (RestClientException e) {
      throw new MeetingNotificationException(FAIL_SEND_MEETING_NOTIFICATION);
    }
  }

  private Map<String, Object> buildBody(MeetingNotification notification) {
    Map<String, Object> body = new HashMap<>();
    body.put("userIds", notification.userIds().stream().map(String::valueOf).toList());
    body.put("title", notification.title());
    body.put("content", notification.content());
    body.put("category", notification.category());
    body.put("webLink", getWebUrl() + "/detail?id=" + notification.meetingId());
    return body;
  }

  private String getWebUrl() {
    return environment.matchesProfiles("prod") ? PROD_WEB_URL : DEV_WEB_URL;
  }
}
