package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.domain.crew.meeting.MeetingFrequency;
import org.sopt.makers.domain.crew.meeting.MeetingType;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandJoinInfo;

public record MeetingDemandJoinInfoRequest(String meetingType, String meetingFrequency) {

  public MeetingDemandJoinInfo toDomainOrNull() {
    if ((meetingType == null || meetingType.isBlank())
        && (meetingFrequency == null || meetingFrequency.isBlank())) {
      return null;
    }
    return new MeetingDemandJoinInfo(
        meetingType == null || meetingType.isBlank() ? null : MeetingType.ofValue(meetingType),
        meetingFrequency == null || meetingFrequency.isBlank()
            ? null
            : MeetingFrequency.ofValue(meetingFrequency));
  }
}
