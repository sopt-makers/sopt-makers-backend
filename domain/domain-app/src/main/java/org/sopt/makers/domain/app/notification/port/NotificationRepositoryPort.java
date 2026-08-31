package org.sopt.makers.domain.app.notification.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.notification.Notification;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.springframework.data.domain.Pageable;

public interface NotificationRepositoryPort {

  Optional<Notification> findByUserIdAndNotificationId(Long userId, String notificationId);

  List<Notification> findAllByUserId(Long userId, Pageable pageable);

  List<Notification> findAllByUserIdAndCategory(
      Long userId, NotificationCategory category, Pageable pageable);

  void saveAll(List<Notification> notifications);

  int markAsRead(Long userId, String notificationId);

  void markAllAsRead(Long userId);

  boolean existsUnreadByUserId(Long userId);

  void deleteAllByUserId(Long userId);
}
