package org.sopt.makers.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdeationStyle {
  SPONTANEOUS("즉흥"),
  DELIBERATE("숙고");

  private final String value;

  public static IdeationStyle fromValue(final String value) {
    if (value == null) return null;
    for (IdeationStyle style : values()) {
      if (style.value.equals(value)) return style;
    }
    throw new IllegalArgumentException("Invalid ideation style: " + value);
  }
}
