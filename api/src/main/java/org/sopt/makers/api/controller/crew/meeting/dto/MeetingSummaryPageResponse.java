package org.sopt.makers.api.controller.crew.meeting.dto;

import java.util.List;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;

public record MeetingSummaryPageResponse(
    List<MeetingSummaryResponse> meetings, MeetingPageMetaResponse meta) {

  public static MeetingSummaryPageResponse from(
      PageResult<MeetingFacade.MeetingSummaryResult> page) {
    return new MeetingSummaryPageResponse(
        page.content().stream().map(MeetingSummaryResponse::from).toList(),
        MeetingPageMetaResponse.from(page));
  }
}
