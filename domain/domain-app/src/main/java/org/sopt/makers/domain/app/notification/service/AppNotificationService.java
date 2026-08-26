package org.sopt.makers.domain.app.notification.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.notification.Notification;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.notification.RegisterNotificationCommand;
import org.sopt.makers.domain.app.notification.exception.NotificationException;
import org.sopt.makers.domain.app.notification.exception.NotificationFailure;
import org.sopt.makers.domain.app.notification.port.NotificationRepositoryPort;
import org.sopt.makers.domain.user.port.AppNotificationUserPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppNotificationService {

  private final NotificationRepositoryPort notificationRepositoryPort;
  private final AppNotificationUserPort appNotificationUserPort;

  public Notification getNotification(Long userId, String notificationId) {
    return notificationRepositoryPort
        .findByUserIdAndNotificationId(userId, notificationId)
        .orElseThrow(() -> new NotificationException(NotificationFailure.NOT_FOUND_NOTIFICATION));
  }

  public List<Notification> getNotifications(
      Long userId, NotificationCategory category, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
    return category == null
        ? notificationRepositoryPort.findAllByUserId(userId, pageable)
        : notificationRepositoryPort.findAllByUserIdAndCategory(userId, category, pageable);
  }

  @Transactional
  public void register(RegisterNotificationCommand command) {
    List<Long> targetUserIds =
        command.isSendAll() ? appNotificationUserPort.findAllUserIds() : command.userIds();
    if (targetUserIds.isEmpty()) {
      return;
    }
    notificationRepositoryPort.saveAll(
        targetUserIds.stream().map(userId -> Notification.create(userId, command)).toList());
  }

  @Transactional
  public void markAsRead(Long userId, String notificationId) {
    if (notificationId == null) {
      notificationRepositoryPort.markAllAsRead(userId);
      return;
    }
    if (notificationRepositoryPort.markAsRead(userId, notificationId) == 0) {
      throw new NotificationException(NotificationFailure.NOT_FOUND_NOTIFICATION);
    }
  }

  public boolean isAllRead(Long userId) {
    return !notificationRepositoryPort.existsUnreadByUserId(userId);
  }

  @Transactional
  public void deleteAllByUserId(Long userId) {
    notificationRepositoryPort.deleteAllByUserId(userId);
  }
}
