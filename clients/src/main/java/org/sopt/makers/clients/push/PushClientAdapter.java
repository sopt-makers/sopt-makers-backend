package org.sopt.makers.clients.push;

import static org.sopt.makers.domain.crew.notification.exception.MeetingNotificationFailure.FAIL_SEND_MEETING_NOTIFICATION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.clients.push.dto.PushSendRequest;
import org.sopt.makers.clients.push.dto.PushTokenManageRequest;
import org.sopt.makers.core.type.ServiceType;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.exception.AlarmException;
import org.sopt.makers.domain.admin.alarm.exception.AlarmFailure;
import org.sopt.makers.domain.admin.alarm.port.AlarmInstantSenderPort;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.PushToken;
import org.sopt.makers.domain.app.push.PushTokenPlatform;
import org.sopt.makers.domain.app.push.exception.PushException;
import org.sopt.makers.domain.app.push.exception.PushFailure;
import org.sopt.makers.domain.app.push.port.PushSenderPort;
import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotification;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandNotificationSenderPort;
import org.sopt.makers.domain.crew.notification.MeetingNotification;
import org.sopt.makers.domain.crew.notification.exception.MeetingNotificationException;
import org.sopt.makers.domain.crew.notification.port.MeetingNotificationSenderPort;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushClientAdapter
    implements AlarmInstantSenderPort,
        PushSenderPort,
        MeetingNotificationSenderPort,
        MeetingDemandNotificationSenderPort {

  private static final String ACTION_SEND = "send";
  private static final String ACTION_REGISTER = "register";
  private static final String ACTION_DELETE = "cancel";
  private static final String DEV_CREW_WEB_URL = "https://sopt-internal-dev.sopt.org/group";
  private static final String PROD_CREW_WEB_URL = "https://playground.sopt.org/group";

  private final PushHttpClient pushHttpClient;
  private final Environment environment;

  @Override
  public void send(Alarm alarm) {
    try {
      pushHttpClient.send(
          ServiceType.ADMIN, alarm.target().sendAction().getValue(), PushSendRequest.from(alarm));
    } catch (RestClientException e) {
      throw new AlarmException(AlarmFailure.FAIL_SEND_ALARM);
    }
  }

  @Override
  public void send(PushMessage message) {
    if (message.userIds().isEmpty()) {
      return;
    }
    try {
      pushHttpClient.send(ServiceType.APP, ACTION_SEND, PushSendRequest.from(message));
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

  @Override
  public void send(MeetingNotification notification) {
    try {
      pushHttpClient.send(
          ServiceType.CREW, ACTION_SEND, PushSendRequest.from(notification, getCrewWebUrl()));
    } catch (RestClientException e) {
      throw new MeetingNotificationException(FAIL_SEND_MEETING_NOTIFICATION);
    }
  }

  @Override
  public void send(MeetingDemandNotification notification) {
    pushHttpClient.send(
        ServiceType.CREW, ACTION_SEND, PushSendRequest.from(notification, getCrewWebUrl()));
  }

  private void manageToken(String action, PushToken pushToken) {
    try {
      pushHttpClient.send(
          ServiceType.APP,
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

  private String getCrewWebUrl() {
    return environment.matchesProfiles("prod") ? PROD_CREW_WEB_URL : DEV_CREW_WEB_URL;
  }
}
