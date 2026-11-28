package org.sopt.makers.domain.app.soptletter;

import java.time.LocalDateTime;

public record SoptLetterTopic(
    Long id,
    String title,
    String subtitle,
    String ctaText,
    boolean isDefault,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    LocalDateTime createdAt) {

  private static final int NEW_TOPIC_DAYS = 7;

  public boolean isActiveAt(LocalDateTime now) {
    if (startedAt == null || endedAt == null) {
      return false;
    }
    return !now.isBefore(startedAt) && !now.isAfter(endedAt);
  }

  public boolean isNewAt(LocalDateTime now) {
    if (createdAt == null) {
      return false;
    }
    return now.isBefore(createdAt.plusDays(NEW_TOPIC_DAYS));
  }
}
