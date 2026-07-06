package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_APPLY_STATUS;

import java.util.Arrays;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public enum MeetingApplyStatus {
  WAITING(0, "대기"),
  APPROVE(1, "승인"),
  REJECT(2, "거절");

  private final int value;
  private final String description;

  MeetingApplyStatus(int value, String description) {
    this.value = value;
    this.description = description;
  }

  public int getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  public static MeetingApplyStatus ofValue(int value) {
    return Arrays.stream(values())
        .filter(status -> status.value == value)
        .findFirst()
        .orElseThrow(() -> new MeetingException(INVALID_APPLY_STATUS));
  }
}
