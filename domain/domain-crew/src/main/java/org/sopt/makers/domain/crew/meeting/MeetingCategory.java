package org.sopt.makers.domain.crew.meeting;

import static org.sopt.makers.domain.crew.meeting.exception.MeetingFailure.INVALID_MEETING_CATEGORY;

import java.util.Arrays;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;

public enum MeetingCategory {
  STUDY("스터디"),
  LECTURE("강연"),
  FLASH("번쩍"),
  EVENT("행사"),
  SEMINAR("세미나");

  private final String value;

  MeetingCategory(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public String getMeetingType() {
    return FLASH.equals(this) ? "번쩍" : "모임";
  }

  public static MeetingCategory ofValue(String value) {
    if (value == null || value.isBlank()) {
      throw new MeetingException(INVALID_MEETING_CATEGORY);
    }
    return Arrays.stream(values())
        .filter(category -> category.name().equalsIgnoreCase(value) || category.value.equals(value))
        .findFirst()
        .orElseThrow(() -> new MeetingException(INVALID_MEETING_CATEGORY));
  }
}
