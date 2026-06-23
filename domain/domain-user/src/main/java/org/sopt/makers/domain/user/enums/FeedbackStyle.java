package org.sopt.makers.domain.user.enums;

import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_WORK_PREFERENCE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.exception.UserException;

@Getter
@RequiredArgsConstructor
public enum FeedbackStyle {
  DIRECT("직설적"),
  INDIRECT("돌려서");

  private final String value;

  public static FeedbackStyle fromValue(final String value) {
    if (value == null) return null;
    for (FeedbackStyle style : values()) {
      if (style.value.equals(value)) return style;
    }
    throw new UserException(INVALID_WORK_PREFERENCE);
  }
}
