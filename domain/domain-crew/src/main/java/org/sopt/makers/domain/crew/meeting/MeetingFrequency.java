package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_MEETING_VALUE;

import java.util.Arrays;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public enum MeetingFrequency {
  LIGHT("가볍게"),
  STEADY("적당히"),
  IMMERSIVE("집중형");

  private final String value;

  MeetingFrequency(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static MeetingFrequency ofValue(String value) {
    if (value == null || value.isBlank()) {
      throw new MeetingException(INVALID_MEETING_VALUE);
    }
    return Arrays.stream(values())
        .filter(
            frequency -> frequency.name().equalsIgnoreCase(value) || frequency.value.equals(value))
        .findFirst()
        .orElseThrow(() -> new MeetingException(INVALID_MEETING_VALUE));
  }
}
