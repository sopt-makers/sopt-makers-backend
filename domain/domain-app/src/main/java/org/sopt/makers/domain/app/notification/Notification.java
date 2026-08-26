package org.sopt.makers.domain.app.notification;

import java.time.LocalDateTime;

public record Notification(
    Long id,
    Long userId,
    String notificationId,
    String title,
    String content,
    NotificationType type,
    NotificationCategory category,
    String deepLink,
    String webLink,
    boolean isRead,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static Notification create(Long userId, RegisterNotificationCommand command) {
    return new Notification(
        null,
        userId,
        command.notificationId(),
        command.title(),
        command.content(),
        command.type(),
        command.category(),
        command.deepLink(),
        command.webLink(),
        false,
        null,
        null);
  }
}
