package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;
import org.springframework.data.domain.Page;

public record OpenedMeetingPageResponse(
    long openedMeetingCount, List<OpenedMeetingResponse> meetings, PageMetaResponse meta) {

  public static OpenedMeetingPageResponse from(Page<MeetingDemandService.OpenedMeeting> page) {
    return new OpenedMeetingPageResponse(
        page.getTotalElements(),
        page.getContent().stream().map(OpenedMeetingResponse::from).toList(),
        PageMetaResponse.from(page));
  }
}
