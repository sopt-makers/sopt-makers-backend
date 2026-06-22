package org.sopt.makers.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkPlace {
  CAFE("카공"),
  HOME("집콕");

  private final String value;

  public static WorkPlace fromValue(final String value) {
    if (value == null) return null;
    for (WorkPlace place : values()) {
      if (place.value.equals(value)) return place;
    }
    throw new IllegalArgumentException("Invalid work place: " + value);
  }
}
