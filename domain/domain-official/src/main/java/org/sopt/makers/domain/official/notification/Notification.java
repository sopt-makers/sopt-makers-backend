package org.sopt.makers.domain.official.notification;

import java.time.LocalDateTime;

public record Notification(Long id, String email, Integer generation, LocalDateTime createdAt) {

  public static Notification create(String email, Integer generation) {
    return new Notification(null, email, generation, null);
  }
}
