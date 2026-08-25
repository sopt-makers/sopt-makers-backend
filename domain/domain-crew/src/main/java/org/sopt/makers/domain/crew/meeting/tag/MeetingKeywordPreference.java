package org.sopt.makers.domain.crew.meeting.tag;

import java.util.List;

public record MeetingKeywordPreference(Long userId, List<MeetingKeywordType> keywordTypes) {

  public MeetingKeywordPreference {
    keywordTypes = keywordTypes == null ? List.of() : List.copyOf(keywordTypes);
  }

  public static MeetingKeywordPreference create(
      Long userId, List<MeetingKeywordType> keywordTypes) {
    return new MeetingKeywordPreference(userId, keywordTypes);
  }

  public MeetingKeywordPreference update(List<MeetingKeywordType> updatedKeywordTypes) {
    return new MeetingKeywordPreference(userId, updatedKeywordTypes);
  }

  public boolean isInterestedIn(List<MeetingKeywordType> meetingKeywordTypes) {
    return meetingKeywordTypes != null
        && keywordTypes.stream().anyMatch(meetingKeywordTypes::contains);
  }
}
