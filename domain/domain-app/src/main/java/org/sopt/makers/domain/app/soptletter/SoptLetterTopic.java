package org.sopt.makers.domain.app.soptletter;

import java.time.LocalDateTime;

public record SoptLetterTopic(
    Long id,
    String title,
    String ctaText,
    boolean isDefault,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    LocalDateTime createdAt) {

  public boolean isActiveAt(LocalDateTime now) {
    if (startedAt == null || endedAt == null) {
      return false;
    }
    return !now.isBefore(startedAt) && !now.isAfter(endedAt);
  }
}
