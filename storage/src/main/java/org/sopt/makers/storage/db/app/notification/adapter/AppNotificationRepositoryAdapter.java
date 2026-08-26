package org.sopt.makers.storage.db.app.notification.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.notification.Notification;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.notification.port.NotificationRepositoryPort;
import org.sopt.makers.storage.db.app.notification.entity.AppNotificationEntity;
import org.sopt.makers.storage.db.app.notification.repository.AppNotificationJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppNotificationRepositoryAdapter implements NotificationRepositoryPort {

  private final AppNotificationJpaRepository appNotificationJpaRepository;

  @Override
  public Optional<Notification> findByUserIdAndNotificationId(Long userId, String notificationId) {
    return appNotificationJpaRepository
        .findByUserIdAndNotificationId(userId, notificationId)
        .map(AppNotificationEntity::toDomain);
  }

  @Override
  public List<Notification> findAllByUserId(Long userId, Pageable pageable) {
    return appNotificationJpaRepository.findAllByUserId(userId, pageable).stream()
        .map(AppNotificationEntity::toDomain)
        .toList();
  }

  @Override
  public List<Notification> findAllByUserIdAndCategory(
      Long userId, NotificationCategory category, Pageable pageable) {
    return appNotificationJpaRepository
        .findAllByUserIdAndCategory(userId, category, pageable)
        .stream()
        .map(AppNotificationEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public void saveAll(List<Notification> notifications) {
    appNotificationJpaRepository.saveAll(
        notifications.stream().map(AppNotificationEntity::from).toList());
  }

  @Override
  @Transactional
  public int markAsRead(Long userId, String notificationId) {
    return appNotificationJpaRepository.markAsRead(userId, notificationId);
  }

  @Override
  @Transactional
  public void markAllAsRead(Long userId) {
    appNotificationJpaRepository.markAllAsRead(userId);
  }

  @Override
  public boolean existsUnreadByUserId(Long userId) {
    return appNotificationJpaRepository.existsByUserIdAndIsReadFalse(userId);
  }

  @Override
  @Transactional
  public void deleteAllByUserId(Long userId) {
    appNotificationJpaRepository.deleteAllByUserId(userId);
  }
}
