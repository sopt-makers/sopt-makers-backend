package org.sopt.makers.storage.db.app.notification.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.notification.Notification;
import org.sopt.makers.domain.app.notification.NotificationCategory;
import org.sopt.makers.domain.app.notification.NotificationType;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "app_notifications",
    indexes = {
      @Index(name = "idx_app_notifications_user_id_created_at", columnList = "user_id, created_at"),
      @Index(
          name = "idx_app_notifications_user_id_notification_id",
          columnList = "user_id, notification_id")
    })
public class AppNotificationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "notification_id", nullable = false, length = 255)
  private String notificationId;

  @Column(name = "notification_title", nullable = false, columnDefinition = "TEXT")
  private String title;

  @Column(name = "notification_content", columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(name = "notification_type", nullable = false, length = 20)
  private NotificationType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "notification_category", nullable = false, length = 20)
  private NotificationCategory category;

  @Column(name = "deep_link", length = 255)
  private String deepLink;

  @Column(name = "web_link", length = 255)
  private String webLink;

  @Column(name = "is_read", nullable = false)
  private boolean isRead;

  private AppNotificationEntity(Notification notification) {
    this.userId = notification.userId();
    this.notificationId = notification.notificationId();
    this.title = notification.title();
    this.content = notification.content();
    this.type = notification.type();
    this.category = notification.category();
    this.deepLink = notification.deepLink();
    this.webLink = notification.webLink();
    this.isRead = notification.isRead();
  }

  public static AppNotificationEntity from(Notification notification) {
    return new AppNotificationEntity(notification);
  }

  public Notification toDomain() {
    return new Notification(
        id,
        userId,
        notificationId,
        title,
        content,
        type,
        category,
        deepLink,
        webLink,
        isRead,
        getCreatedAt(),
        getUpdatedAt());
  }
}
