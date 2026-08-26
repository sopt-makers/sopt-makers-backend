package org.sopt.makers.storage.db.app.notification.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.storage.db.app.notification.entity.AppNotificationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppNotificationJpaRepository extends JpaRepository<AppNotificationEntity, Long> {

  Optional<AppNotificationEntity> findByUserIdAndNotificationId(Long userId, String notificationId);

  List<AppNotificationEntity> findAllByUserId(Long userId, Pageable pageable);

  List<AppNotificationEntity> findAllByUserIdAndCategory(
      Long userId, NotificationCategory category, Pageable pageable);

  boolean existsByUserIdAndIsReadFalse(Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "UPDATE AppNotificationEntity n SET n.isRead = true, n.updatedAt = CURRENT_TIMESTAMP"
          + " WHERE n.userId = :userId AND n.notificationId = :notificationId")
  int markAsRead(@Param("userId") Long userId, @Param("notificationId") String notificationId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "UPDATE AppNotificationEntity n SET n.isRead = true, n.updatedAt = CURRENT_TIMESTAMP"
          + " WHERE n.userId = :userId AND n.isRead = false")
  int markAllAsRead(@Param("userId") Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM AppNotificationEntity n WHERE n.userId = :userId")
  void deleteAllByUserId(@Param("userId") Long userId);
}
