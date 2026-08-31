package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;

public record ToggleMeetingDemandWaitResponse(int waitCount, Boolean isWaiting) {

  public static ToggleMeetingDemandWaitResponse from(MeetingDemandService.WaitResult result) {
    return new ToggleMeetingDemandWaitResponse(result.waitCount(), result.isWaiting());
  }
}
