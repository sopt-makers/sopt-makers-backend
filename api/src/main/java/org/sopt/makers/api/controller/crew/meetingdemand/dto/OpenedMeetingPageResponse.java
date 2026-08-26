package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import java.util.List;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;

public record OpenedMeetingPageResponse(
    long openedMeetingCount, List<OpenedMeetingResponse> meetings, PageMetaResponse meta) {

  public static OpenedMeetingPageResponse from(
      PageResult<MeetingDemandService.OpenedMeeting> page) {
    return new OpenedMeetingPageResponse(
        page.totalElements(),
        page.content().stream().map(OpenedMeetingResponse::from).toList(),
        PageMetaResponse.from(page));
  }
}
