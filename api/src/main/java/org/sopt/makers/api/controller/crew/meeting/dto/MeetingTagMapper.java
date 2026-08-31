package org.sopt.makers.api.controller.crew.meeting.dto;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.sopt.makers.domain.crew.meeting.tag.WelcomeMessageType;

public final class MeetingTagMapper {

  private MeetingTagMapper() {}

  public static List<WelcomeMessageType> toWelcomeMessageTypes(List<String> values) {
    if (values == null) {
      return null;
    }
    return values.stream().map(WelcomeMessageType::ofValue).toList();
  }

  public static List<MeetingKeywordType> toMeetingKeywordTypes(List<String> values) {
    if (values == null) {
      return null;
    }
    return values.stream().map(MeetingKeywordType::ofValue).toList();
  }

  public static List<String> fromWelcomeMessageTypes(List<WelcomeMessageType> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream().map(WelcomeMessageType::getValue).toList();
  }

  public static List<String> fromMeetingKeywordTypes(List<MeetingKeywordType> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream().map(MeetingKeywordType::getValue).toList();
  }
}
