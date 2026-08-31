package org.sopt.makers.domain.crew.meeting.demand.service;

import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotification;
import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotificationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MeetingDemandNotificationPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public MeetingDemandNotificationPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void publish(MeetingDemandNotification notification) {
    publish(notification, null);
  }

  public void publishOpened(MeetingDemandNotification notification, Long openedNotificationId) {
    publish(notification, openedNotificationId);
  }

  private void publish(MeetingDemandNotification notification, Long openedNotificationId) {
    if (notification == null || notification.userIds().isEmpty()) {
      return;
    }
    eventPublisher.publishEvent(
        new MeetingDemandNotificationEvent(notification, openedNotificationId));
  }
}
