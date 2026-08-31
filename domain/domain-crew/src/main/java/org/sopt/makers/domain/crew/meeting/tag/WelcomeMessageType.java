package org.sopt.makers.domain.crew.meeting.tag;

import static org.sopt.makers.domain.crew.meeting.tag.exception.MeetingTagFailure.INVALID_WELCOME_MESSAGE_TYPE;

import java.util.Arrays;
import org.sopt.makers.domain.crew.meeting.tag.exception.MeetingTagException;

public enum WelcomeMessageType {
  YB_WELCOME("YB 환영"),
  OB_WELCOME("OB 환영"),
  FIRST_MEETING_WELCOME("초면 환영"),
  BEGINNER_WELCOME("입문자 환영"),
  EXPERIENCED_WELCOME("숙련자 환영");

  private final String value;

  WelcomeMessageType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static WelcomeMessageType ofValue(String value) {
    if (value == null || value.isBlank()) {
      throw new MeetingTagException(INVALID_WELCOME_MESSAGE_TYPE);
    }
    String normalized = value.trim();
    return Arrays.stream(values())
        .filter(type -> type.name().equalsIgnoreCase(normalized) || type.value.equals(normalized))
        .findFirst()
        .orElseThrow(() -> new MeetingTagException(INVALID_WELCOME_MESSAGE_TYPE));
  }
}
