package org.sopt.makers.api.controller.app.notification.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.app.notification.Notification;

public record NotificationSimpleResponse(
    String notificationId,
    Long userId,
    String title,
    String content,
    String category,
    boolean isRead,
    LocalDateTime createdAt) {

  public static NotificationSimpleResponse of(Notification notification) {
    return new NotificationSimpleResponse(
        notification.notificationId(),
        notification.userId(),
        notification.title(),
        notification.content(),
        notification.category().name(),
        notification.isRead(),
        notification.createdAt());
  }
}
