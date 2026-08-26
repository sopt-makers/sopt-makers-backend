package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;
import org.springframework.data.domain.Page;

public record MeetingDemandPageResponse(
    List<MeetingDemandSummaryResponse> meetingDemands, PageMetaResponse meta) {

  public static MeetingDemandPageResponse from(
      Page<MeetingDemandService.MeetingDemandSummary> page) {
    return new MeetingDemandPageResponse(
        page.getContent().stream().map(MeetingDemandSummaryResponse::from).toList(),
        PageMetaResponse.from(page));
  }
}
