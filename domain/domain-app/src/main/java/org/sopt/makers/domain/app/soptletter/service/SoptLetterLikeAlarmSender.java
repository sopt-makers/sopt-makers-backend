package org.sopt.makers.domain.app.soptletter.service;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.home.ActivityStatus;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.port.PushSenderPort;
import org.sopt.makers.domain.app.soptletter.SoptLetterLikeEvent;
import org.sopt.makers.domain.user.port.AppSoptLetterUserPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class SoptLetterLikeAlarmSender {

  private static final String LIKE_NOTIFICATION_TITLE_KEY = "likeNotificationTitle";
  private static final String LIKE_NOTIFICATION_CONTENT_KEY = "likeNotificationContent";
  private static final String DEEP_LINK_FORMAT =
      "soptletter?topicId=%d&messageId=%d&pushType=soptletter_liked";

  private final PushSenderPort pushSenderPort;
  private final OperationConfigService operationConfigService;
  private final AppSoptLetterUserPort appSoptLetterUserPort;
  private final long currentGeneration;

  public SoptLetterLikeAlarmSender(
      PushSenderPort pushSenderPort,
      OperationConfigService operationConfigService,
      AppSoptLetterUserPort appSoptLetterUserPort,
      @Value("${sopt.current.generation}") long currentGeneration) {
    this.pushSenderPort = pushSenderPort;
    this.operationConfigService = operationConfigService;
    this.appSoptLetterUserPort = appSoptLetterUserPort;
    this.currentGeneration = currentGeneration;
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(SoptLetterLikeEvent event) {
    try {
      if (!isActiveMember(event.authorUserId())) {
        return;
      }

      String title =
          operationConfigService.getValue(
              OperationConfigCategory.SOPT_LETTER, LIKE_NOTIFICATION_TITLE_KEY);
      String content =
          operationConfigService.getValue(
              OperationConfigCategory.SOPT_LETTER, LIKE_NOTIFICATION_CONTENT_KEY);
      if (title.isBlank() || content.isBlank()) {
        throw new IllegalArgumentException("좋아요 알림 제목과 내용은 비어 있을 수 없습니다.");
      }

      pushSenderPort.send(
          new PushMessage(
              Set.of(event.authorUserId()),
              title,
              content,
              NotificationCategory.NOTICE,
              DEEP_LINK_FORMAT.formatted(event.topicId(), event.letterId()),
              null));
    } catch (Exception e) {
      log.error("솝레터 좋아요 알림 전송 실패: messageId={}", event.letterId(), e);
    }
  }

  private boolean isActiveMember(Long userId) {
    return appSoptLetterUserPort
        .findWithActivities(userId)
        .map(user -> ActivityStatus.of(user, currentGeneration) == ActivityStatus.ACTIVE)
        .orElse(false);
  }
}
