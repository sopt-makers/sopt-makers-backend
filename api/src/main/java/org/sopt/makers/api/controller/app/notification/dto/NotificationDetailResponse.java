package org.sopt.makers.api.controller.app.notification.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.app.notification.Notification;

public record NotificationDetailResponse(
    String notificationId,
    Long userId,
    String title,
    String content,
    String deepLink,
    String webLink,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static NotificationDetailResponse of(Notification notification) {
    return new NotificationDetailResponse(
        notification.notificationId(),
        notification.userId(),
        notification.title(),
        notification.content(),
        notification.deepLink(),
        notification.webLink(),
        notification.createdAt(),
        notification.updatedAt());
  }
}
