package org.sopt.makers.domain.user.enums;

import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_WORK_PREFERENCE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.exception.UserException;

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
    throw new UserException(INVALID_WORK_PREFERENCE);
  }
}
