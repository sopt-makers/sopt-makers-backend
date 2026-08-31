package org.sopt.makers.api.controller.crew.user.dto;

import java.util.List;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingTagMapper;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordPreference;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;

public record MeetingKeywordPreferenceResponse(List<String> keywords) {

  public static MeetingKeywordPreferenceResponse from(MeetingKeywordPreference preference) {
    return from(preference.keywordTypes());
  }

  public static MeetingKeywordPreferenceResponse from(List<MeetingKeywordType> keywords) {
    return new MeetingKeywordPreferenceResponse(MeetingTagMapper.fromMeetingKeywordTypes(keywords));
  }
}
