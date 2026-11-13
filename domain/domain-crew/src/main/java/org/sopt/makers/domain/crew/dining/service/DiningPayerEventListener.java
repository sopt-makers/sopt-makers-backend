package org.sopt.makers.domain.crew.dining.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.dining.event.DiningPayerApprovedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningPayerRejectedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningPayerRequestedEvent;
import org.sopt.makers.domain.crew.dining.event.DiningPayerRevokedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DiningPayerEventListener {

  private final DiningNotificationSender diningNotificationSender;

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDiningPayerRequested(DiningPayerRequestedEvent event) {
    diningNotificationSender.sendPayerRequestedNotification(event);
  }

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDiningPayerApproved(DiningPayerApprovedEvent event) {
    diningNotificationSender.sendPayerApprovedNotification(event);
  }

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDiningPayerRejected(DiningPayerRejectedEvent event) {
    diningNotificationSender.sendPayerRejectedNotification(event);
  }

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDiningPayerRevoked(DiningPayerRevokedEvent event) {
    diningNotificationSender.sendPayerRevokedNotification(event);
  }
}
