package org.sopt.makers.domain.crew.meeting.tag;

import java.time.LocalDateTime;
import java.util.List;

public record MeetingTag(
    Long id,
    MeetingTagType type,
    Long meetingId,
    Long flashId,
    List<WelcomeMessageType> welcomeMessageTypes,
    List<MeetingKeywordType> meetingKeywordTypes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public MeetingTag {
    welcomeMessageTypes =
        welcomeMessageTypes == null ? List.of() : List.copyOf(welcomeMessageTypes);
    meetingKeywordTypes =
        meetingKeywordTypes == null ? List.of() : List.copyOf(meetingKeywordTypes);
  }

  public static MeetingTag createGeneralMeetingTag(
      Long meetingId,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {
    return new MeetingTag(
        null,
        MeetingTagType.MEETING,
        meetingId,
        null,
        welcomeMessageTypes,
        meetingKeywordTypes,
        null,
        null);
  }

  public static MeetingTag createFlashTag(
      Long flashId,
      Long meetingId,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {
    return new MeetingTag(
        null,
        MeetingTagType.FLASH,
        meetingId,
        flashId,
        welcomeMessageTypes,
        meetingKeywordTypes,
        null,
        null);
  }

  public MeetingTag update(
      List<WelcomeMessageType> updatedWelcomeMessageTypes,
      List<MeetingKeywordType> updatedMeetingKeywordTypes) {
    return new MeetingTag(
        id,
        type,
        meetingId,
        flashId,
        updatedWelcomeMessageTypes == null ? welcomeMessageTypes : updatedWelcomeMessageTypes,
        updatedMeetingKeywordTypes == null ? meetingKeywordTypes : updatedMeetingKeywordTypes,
        createdAt,
        updatedAt);
  }
}
