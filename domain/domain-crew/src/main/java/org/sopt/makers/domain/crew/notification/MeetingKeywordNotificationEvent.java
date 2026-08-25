package org.sopt.makers.domain.crew.notification;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;

public record MeetingKeywordNotificationEvent(
    List<MeetingKeywordType> meetingKeywordTypes,
    Long meetingId,
    String meetingTitle,
    MeetingCategory meetingCategory) {

  public MeetingKeywordNotificationEvent {
    meetingKeywordTypes =
        meetingKeywordTypes == null ? List.of() : List.copyOf(meetingKeywordTypes);
  }
}
