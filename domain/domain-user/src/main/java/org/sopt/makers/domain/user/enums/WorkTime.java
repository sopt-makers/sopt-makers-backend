package org.sopt.makers.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkTime {
  MORNING("아침"),
  NIGHT("밤");

  private final String value;

  public static WorkTime fromValue(final String value) {
    if (value == null) return null;
    for (WorkTime time : values()) {
      if (time.value.equals(value)) return time;
    }
    throw new IllegalArgumentException("Invalid work time: " + value);
  }
}
