package org.sopt.makers.domain.app.poke.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.poke.PokeEvent;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.port.PushSenderPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PokeAlarmSender {

  private static final String ALARM_TITLE = "콕 찌르기";
  private static final String ALARM_CONTENT = "누군가가 콕 찔렀어요. 확인해보세요!";
  private static final String ALARM_DEEP_LINK = "poke/notification-list";

  private final PushSenderPort pushSenderPort;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(final PokeEvent event) {
    try {
      pushSenderPort.send(
          new PushMessage(
              Set.of(event.pokedUserId()),
              ALARM_TITLE,
              ALARM_CONTENT,
              NotificationCategory.NEWS,
              ALARM_DEEP_LINK,
              null));
    } catch (Exception e) {
      log.error("찌르기 알림 발송 실패. pokedUserId={}", event.pokedUserId(), e);
    }
  }
}
