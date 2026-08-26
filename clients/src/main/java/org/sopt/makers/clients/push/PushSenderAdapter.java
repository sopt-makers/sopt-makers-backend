package org.sopt.makers.clients.push;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.clients.alarm.AlarmProperty;
import org.sopt.makers.clients.notification.NotificationHttpClient;
import org.sopt.makers.clients.push.dto.PushSendRequest;
import org.sopt.makers.clients.push.dto.PushTokenManageRequest;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.PushToken;
import org.sopt.makers.domain.app.push.PushTokenPlatform;
import org.sopt.makers.domain.app.push.exception.PushException;
import org.sopt.makers.domain.app.push.exception.PushFailure;
import org.sopt.makers.domain.app.push.port.PushSenderPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushSenderAdapter implements PushSenderPort {

  private static final String ACTION_SEND = "send";
  private static final String ACTION_REGISTER = "register";
  private static final String ACTION_DELETE = "cancel";

  private final NotificationHttpClient notificationHttpClient;
  private final AlarmProperty property;

  @Override
  public void send(PushMessage message) {
    if (message.userIds().isEmpty()) {
      return;
    }
    try {
      notificationHttpClient.send(
          property.appHeaderService(), ACTION_SEND, PushSendRequest.from(message));
    } catch (RestClientException e) {
      log.warn("푸시 발송 실패 - title={}, 대상 {}명", message.title(), message.userIds().size(), e);
      throw new PushException(PushFailure.FAIL_SEND_PUSH);
    }
  }

  @Override
  public void register(PushToken pushToken) {
    manageToken(ACTION_REGISTER, pushToken);
  }

  @Override
  public void delete(PushToken pushToken) {
    manageToken(ACTION_DELETE, pushToken);
  }

  private void manageToken(String action, PushToken pushToken) {
    try {
      notificationHttpClient.send(
          property.appHeaderService(),
          action,
          PushTokenManageRequest.from(pushToken),
          toPlatformHeader(pushToken.platform()));
    } catch (RestClientException e) {
      log.warn("푸시 토큰 {} 실패 - userId={}", action, pushToken.userId(), e);
      throw new PushException(PushFailure.FAIL_MANAGE_PUSH_TOKEN);
    }
  }

  private String toPlatformHeader(PushTokenPlatform platform) {
    return platform == PushTokenPlatform.IOS ? "iOS" : "Android";
  }
}
