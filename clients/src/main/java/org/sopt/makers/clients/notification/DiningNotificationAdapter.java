package org.sopt.makers.clients.notification;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.crew.dining.port.DiningNotificationPort;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiningNotificationAdapter implements DiningNotificationPort {
  private final NotificationHttpClient client;
  private final Environment environment;

  @Override
  public boolean send(List<String> receivers, String title, String content, String path) {
    String base =
        environment.matchesProfiles("prod")
            ? "https://playground.sopt.org/group"
            : "https://sopt-internal-dev.sopt.org/group";
    try {
      client.send(
          "crew",
          "send",
          Map.of(
              "userIds",
              receivers,
              "title",
              title,
              "content",
              content,
              "category",
              "NEWS",
              "webLink",
              base + path));
      return true;
    } catch (RestClientException e) {
      log.error("회식 알림 발송 실패", e);
      return false;
    }
  }
}
