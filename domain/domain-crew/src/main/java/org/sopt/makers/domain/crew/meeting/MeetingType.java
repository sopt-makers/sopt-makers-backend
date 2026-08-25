package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_MEETING_VALUE;

import java.util.Arrays;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public enum MeetingType {
  ONLINE("온라인"),
  OFFLINE("오프라인"),
  ONLINE_OFFLINE("온-오프");

  private final String value;

  MeetingType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static MeetingType ofValue(String value) {
    if (value == null || value.isBlank()) {
      throw new MeetingException(INVALID_MEETING_VALUE);
    }
    return Arrays.stream(values())
        .filter(type -> type.name().equalsIgnoreCase(value) || type.value.equals(value))
        .findFirst()
        .orElseThrow(() -> new MeetingException(INVALID_MEETING_VALUE));
  }
}
