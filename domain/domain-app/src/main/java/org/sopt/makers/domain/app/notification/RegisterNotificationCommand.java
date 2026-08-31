package org.sopt.makers.domain.app.notification;

import java.util.List;

public record RegisterNotificationCommand(
    List<Long> userIds,
    String notificationId,
    String title,
    String content,
    NotificationType type,
    NotificationCategory category,
    String deepLink,
    String webLink) {

  public RegisterNotificationCommand {
    userIds = userIds == null ? List.of() : List.copyOf(userIds);
  }

  public boolean isSendAll() {
    return type == NotificationType.SEND_ALL;
  }
}
