package org.sopt.makers.clients.alarm;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.clients.notification.NotificationHttpClient;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.AlarmLinkType;
import org.sopt.makers.domain.admin.alarm.AlarmTargetType;
import org.sopt.makers.domain.admin.alarm.exception.AlarmException;
import org.sopt.makers.domain.admin.alarm.exception.AlarmFailure;
import org.sopt.makers.domain.admin.alarm.port.AlarmInstantSenderPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class AlarmInstantSenderAdapter implements AlarmInstantSenderPort {

  private final NotificationHttpClient notificationHttpClient;
  private final AlarmProperty alarmProperty;

  @Override
  public void send(Alarm alarm) {
    try {
      Map<Object, Object> body = buildBody(alarm);
      notificationHttpClient.send(
          alarmProperty.headerService(), alarm.target().sendAction().getValue(), body);
    } catch (RestClientException e) {
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
}
