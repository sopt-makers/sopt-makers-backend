package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;

public record CreateMeetingDemandResponse(Long meetingDemandId) {

  public static CreateMeetingDemandResponse from(MeetingDemand meetingDemand) {
    return new CreateMeetingDemandResponse(meetingDemand.id());
  }
}
