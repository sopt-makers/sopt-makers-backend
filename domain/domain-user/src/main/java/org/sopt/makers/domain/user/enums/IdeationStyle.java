package org.sopt.makers.domain.user.enums;

import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_WORK_PREFERENCE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.exception.UserException;

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
    throw new UserException(INVALID_WORK_PREFERENCE);
  }
}
