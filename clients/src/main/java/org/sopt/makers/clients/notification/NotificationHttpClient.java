package org.sopt.makers.clients.notification;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.clients.alarm.AlarmProperty;
import org.sopt.makers.core.type.ServiceType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NotificationHttpClient {

  private final RestTemplate restTemplate;
  private final AlarmProperty alarmProperty;

  public void send(ServiceType serviceType, String action, Object body) {
    send(serviceType, action, body, null);
  }

  public void send(ServiceType serviceType, String action, Object body, String platform) {
    restTemplate.postForEntity(
        alarmProperty.url(),
        new HttpEntity<>(body, buildHeaders(serviceType, action, platform)),
        Object.class);
  }

  private HttpHeaders buildHeaders(ServiceType serviceType, String action, String platform) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add("x-api-key", alarmProperty.key());
    headers.add("action", action);
    headers.add("transactionId", UUID.randomUUID().toString());
    headers.add("service", serviceType.getValue());
    if (platform != null) {
      headers.add("platform", platform);
    }
    return headers;
  }
}
