package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_APPLY_TYPE;

import java.util.Arrays;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public enum MeetingApplyType {
  APPLY(0),
  INVITE(1);

  private final int value;

  MeetingApplyType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static MeetingApplyType ofValue(int value) {
    return Arrays.stream(values())
        .filter(type -> type.value == value)
        .findFirst()
        .orElseThrow(() -> new MeetingException(INVALID_APPLY_TYPE));
  }
}
