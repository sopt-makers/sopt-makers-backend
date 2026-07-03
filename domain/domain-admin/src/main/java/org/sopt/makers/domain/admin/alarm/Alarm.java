package org.sopt.makers.domain.admin.alarm;

import java.time.LocalDateTime;

public record Alarm(
    Long id,
    AlarmStatus status,
    AlarmType type,
    AlarmTarget target,
    AlarmContent content,
    LocalDateTime intendedAt,
    LocalDateTime sendAt,
    LocalDateTime createdAt) {

  public static Alarm instant(AlarmTarget target, AlarmContent content) {
    return new Alarm(null, AlarmStatus.COMPLETED, AlarmType.INSTANT, target, content, LocalDateTime.now(), null, null);
  }

  public static Alarm scheduled(AlarmTarget target, AlarmContent content, LocalDateTime intendedAt) {
    return new Alarm(null, AlarmStatus.SCHEDULED, AlarmType.RESERVED, target, content, intendedAt, null, null);
  }

  public Alarm withTarget(AlarmTarget resolvedTarget) {
    return new Alarm(id, status, type, resolvedTarget, content, intendedAt, sendAt, createdAt);
  }

  public Alarm complete(LocalDateTime successAt) {
    return new Alarm(id, AlarmStatus.COMPLETED, type, target, content, intendedAt, successAt, createdAt);
  }

  public boolean isScheduled() {
    return AlarmStatus.SCHEDULED.equals(status);
  }
}
