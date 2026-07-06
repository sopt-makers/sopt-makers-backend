package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_MEETING_STATUS;

import java.util.Arrays;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public enum MeetingStatus {
  BEFORE_START(0),
  APPLY_ABLE(1),
  RECRUITMENT_COMPLETE(2);

  private final int value;

  MeetingStatus(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static MeetingStatus ofValue(int value) {
    return Arrays.stream(values())
        .filter(status -> status.value == value)
        .findFirst()
        .orElseThrow(() -> new MeetingException(INVALID_MEETING_STATUS));
  }
}
