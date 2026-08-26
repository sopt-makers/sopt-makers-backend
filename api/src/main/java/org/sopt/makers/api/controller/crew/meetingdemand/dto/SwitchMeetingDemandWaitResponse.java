package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;

public record SwitchMeetingDemandWaitResponse(int waitCount, Boolean isWaiting) {

  public static SwitchMeetingDemandWaitResponse from(MeetingDemandService.WaitResult result) {
    return new SwitchMeetingDemandWaitResponse(result.waitCount(), result.isWaiting());
  }
}
