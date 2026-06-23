package org.sopt.makers.domain.user.enums;

import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_WORK_PREFERENCE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.exception.UserException;

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
    throw new UserException(INVALID_WORK_PREFERENCE);
  }
}
