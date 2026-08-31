package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import java.util.List;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;

public record MeetingDemandPageResponse(
    List<MeetingDemandSummaryResponse> meetingDemands, PageMetaResponse meta) {

  public static MeetingDemandPageResponse from(
      PageResult<MeetingDemandService.MeetingDemandSummary> page) {
    return new MeetingDemandPageResponse(
        page.content().stream().map(MeetingDemandSummaryResponse::from).toList(),
        PageMetaResponse.from(page));
  }
}
