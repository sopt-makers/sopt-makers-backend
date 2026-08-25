package org.sopt.makers.api.controller.crew.user.dto;

import java.util.List;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingTagMapper;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;

public record UpdateMeetingKeywordPreferenceRequest(List<String> keywords) {

  public List<MeetingKeywordType> toDomain() {
    List<MeetingKeywordType> result = MeetingTagMapper.toMeetingKeywordTypes(keywords);
    return result == null ? List.of() : result;
  }
}
