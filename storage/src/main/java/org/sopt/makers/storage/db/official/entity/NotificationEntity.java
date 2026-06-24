package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.sopt.makers.domain.official.notification.Notification;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "\"Notification\"")
public class NotificationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"id\"", nullable = false)
  private Long id;

  @Column(name = "\"email\"", nullable = false)
  private String email;

  @Column(name = "\"generation\"", nullable = false)
  private Integer generation;

  @CreationTimestamp
  @Column(name = "\"createdAt\"", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Builder(access = PRIVATE)
  private NotificationEntity(Long id, String email, Integer generation, LocalDateTime createdAt) {
    this.id = id;
    this.email = email;
    this.generation = generation;
    this.createdAt = createdAt;
  }

  public Notification toDomain() {
    return new Notification(id, email, generation, createdAt);
  }

  public static NotificationEntity fromDomain(Notification notification) {
    return NotificationEntity.builder()
        .id(notification.id())
        .email(notification.email())
        .generation(notification.generation())
        .createdAt(notification.createdAt())
        .build();
  }
}
