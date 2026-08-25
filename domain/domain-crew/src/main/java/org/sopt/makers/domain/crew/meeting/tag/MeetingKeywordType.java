package org.sopt.makers.domain.crew.meeting.tag;

import static org.sopt.makers.domain.crew.meeting.tag.exception.MeetingTagFailure.INVALID_MEETING_KEYWORD_TYPE;

import java.util.Arrays;
import org.sopt.makers.domain.crew.meeting.tag.exception.MeetingTagException;

public enum MeetingKeywordType {
  EXERCISE("운동"),
  FOOD("먹방"),
  HOBBY("취미"),
  STUDY("학습"),
  SELF_DEVELOPMENT("자기계발"),
  NETWORKING("네트워킹"),
  ETC("기타");

  private final String value;

  MeetingKeywordType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static MeetingKeywordType ofValue(String value) {
    if (value == null || value.isBlank()) {
      throw new MeetingTagException(INVALID_MEETING_KEYWORD_TYPE);
    }
    String normalized = value.trim();
    return Arrays.stream(values())
        .filter(type -> type.name().equalsIgnoreCase(normalized) || type.value.equals(normalized))
        .findFirst()
        .orElseThrow(() -> new MeetingTagException(INVALID_MEETING_KEYWORD_TYPE));
  }
}
