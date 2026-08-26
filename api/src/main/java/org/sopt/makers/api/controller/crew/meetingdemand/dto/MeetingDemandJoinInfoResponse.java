package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandJoinInfo;

public record MeetingDemandJoinInfoResponse(String meetingType, String meetingFrequency) {

  public static MeetingDemandJoinInfoResponse from(MeetingDemandJoinInfo joinInfo) {
    if (joinInfo == null) {
      return null;
    }
    return new MeetingDemandJoinInfoResponse(
        joinInfo.meetingType() == null ? null : joinInfo.meetingType().getValue(),
        joinInfo.meetingFrequency() == null ? null : joinInfo.meetingFrequency().getValue());
  }
}
