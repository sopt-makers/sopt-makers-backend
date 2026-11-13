package org.sopt.makers.domain.crew.dining.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.dining.event.DiningRoundCreatedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DiningRoundCreatedEventListener {

  private final DiningNotificationSender diningNotificationSender;

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDiningRoundCreatedEvent(DiningRoundCreatedEvent event) {
    diningNotificationSender.sendNewRoundNotification(event);
  }
}
