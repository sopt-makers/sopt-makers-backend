package org.sopt.makers.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommunicationStyle {
  BATCH("몰아서"),
  DISTRIBUTED("나눠서");

  private final String value;

  public static CommunicationStyle fromValue(final String value) {
    if (value == null) return null;
    for (CommunicationStyle style : values()) {
      if (style.value.equals(value)) return style;
    }
    throw new IllegalArgumentException("Invalid communication style: " + value);
  }
}
