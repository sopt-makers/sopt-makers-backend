package org.sopt.makers.domain.crew.meeting.demand.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotificationEvent;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandNotificationSenderPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingDemandNotificationEventListener {

  private final MeetingDemandNotificationSenderPort notificationSenderPort;
  private final MeetingDemandOpenedNotificationService openedNotificationService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(MeetingDemandNotificationEvent event) {
    try {
      notificationSenderPort.send(event.notification());
      if (event.openedNotificationId() != null) {
        openedNotificationService.markSent(event.openedNotificationId());
      }
    } catch (Exception exception) {
      log.error("모임 수요 알림 발송 실패. openedNotificationId={}", event.openedNotificationId(), exception);
    }
  }
}
