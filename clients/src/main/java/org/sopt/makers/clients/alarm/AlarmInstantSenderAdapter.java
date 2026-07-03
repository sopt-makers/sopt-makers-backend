package org.sopt.makers.clients.alarm;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.AlarmLinkType;
import org.sopt.makers.domain.admin.alarm.AlarmTargetType;
import org.sopt.makers.domain.admin.alarm.exception.AlarmException;
import org.sopt.makers.domain.admin.alarm.exception.AlarmFailure;
import org.sopt.makers.domain.admin.alarm.port.AlarmInstantSenderPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AlarmInstantSenderAdapter implements AlarmInstantSenderPort {

  private final RestTemplate restTemplate;
  private final AlarmProperty alarmProperty;

  @Override
  public void send(Alarm alarm) {
    try {
      Map<Object, Object> body = buildBody(alarm);
      HttpHeaders headers = buildHeaders(alarm);
      restTemplate.postForEntity(alarmProperty.url(), new HttpEntity<>(body, headers), Object.class);
    } catch (HttpServerErrorException | HttpClientErrorException e) {
      throw new AlarmException(AlarmFailure.FAIL_SEND_ALARM);
    }
  }

  private Map<Object, Object> buildBody(Alarm alarm) {
    Map<Object, Object> body = new HashMap<>();
    body.put("title", alarm.content().title());
    body.put("content", alarm.content().content());
    body.put("category", alarm.content().category());

    boolean isTargetAll = AlarmTargetType.ALL.equals(alarm.target().targetType());
    if (!isTargetAll) {
      body.put("userIds", alarm.target().targetIds());
    }

    AlarmLinkType linkType = alarm.content().linkType();
    if (AlarmLinkType.WEB.equals(linkType)) {
      body.put("webLink", alarm.content().linkPath());
    } else if (AlarmLinkType.APP.equals(linkType)) {
      body.put("deepLink", alarm.content().linkPath());
    }
    return body;
  }

  private HttpHeaders buildHeaders(Alarm alarm) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add("action", alarm.target().sendAction().getValue());
    headers.add("transactionId", UUID.randomUUID().toString());
    headers.add("service", alarmProperty.headerService());
    headers.add("x-api-key", alarmProperty.key());
    return headers;
  }
}
