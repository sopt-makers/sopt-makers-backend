package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;

public record MeetingDemandSummaryResponse(
    Long id,
    String shortIntro,
    String expectation,
    String status,
    Boolean isMine,
    int waitCount,
    Boolean isWaiting) {

  public static MeetingDemandSummaryResponse from(
      MeetingDemandService.MeetingDemandSummary summary) {
    MeetingDemand demand = summary.meetingDemand();
    return new MeetingDemandSummaryResponse(
        demand.id(),
        demand.shortIntro(),
        demand.expectation(),
        demand.status().name(),
        summary.isMine(),
        demand.waitCount(),
        summary.isWaiting());
  }
}
