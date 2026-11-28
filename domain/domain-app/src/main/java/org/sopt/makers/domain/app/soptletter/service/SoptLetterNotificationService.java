package org.sopt.makers.domain.app.soptletter.service;

import java.util.HashSet;
import java.util.Set;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.port.PushSenderPort;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterProfileRepositoryPort;
import org.sopt.makers.domain.user.port.AppSoptLetterUserPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SoptLetterNotificationService {

  private final AppSoptLetterUserPort appSoptLetterUserPort;
  private final SoptLetterProfileRepositoryPort soptLetterProfileRepositoryPort;
  private final PushSenderPort pushSenderPort;
  private final int currentGeneration;

  public SoptLetterNotificationService(
      AppSoptLetterUserPort appSoptLetterUserPort,
      SoptLetterProfileRepositoryPort soptLetterProfileRepositoryPort,
      PushSenderPort pushSenderPort,
      @Value("${sopt.current.generation}") int currentGeneration) {
    this.appSoptLetterUserPort = appSoptLetterUserPort;
    this.soptLetterProfileRepositoryPort = soptLetterProfileRepositoryPort;
    this.pushSenderPort = pushSenderPort;
    this.currentGeneration = currentGeneration;
  }

  public void sendUnwrittenReminder(String title, String content, String deepLink) {
    Set<Long> targetUserIds = new HashSet<>(findCurrentGenerationUserIds());
    targetUserIds.removeAll(soptLetterProfileRepositoryPort.findWrittenUserIds(targetUserIds));
    send(targetUserIds, title, content, deepLink);
  }

  public void sendNewTopic(String title, String content, String deepLink) {
    send(findCurrentGenerationUserIds(), title, content, deepLink);
  }

  private Set<Long> findCurrentGenerationUserIds() {
    return appSoptLetterUserPort.findSoptUserIdsByGeneration(currentGeneration);
  }

  private void send(Set<Long> targetUserIds, String title, String content, String deepLink) {
    if (targetUserIds.isEmpty()) {
      return;
    }
    pushSenderPort.send(
        new PushMessage(
            targetUserIds, title, content, NotificationCategory.NOTICE, deepLink, null));
  }
}
