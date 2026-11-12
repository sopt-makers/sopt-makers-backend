package org.sopt.makers.api.controller.crew.meeting.dto;

import java.util.List;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;

public record JoinedMeetingPageResponse(
    List<JoinedMeetingResponse> meetings, MeetingPageMetaResponse meta) {

  public static JoinedMeetingPageResponse from(PageResult<MeetingFacade.JoinedMeetingResult> page) {
    return new JoinedMeetingPageResponse(
        page.content().stream().map(JoinedMeetingResponse::from).toList(),
        MeetingPageMetaResponse.from(page));
  }

  public record JoinedMeetingResponse(String role, MeetingSummaryResponse meeting) {

    public static JoinedMeetingResponse from(MeetingFacade.JoinedMeetingResult result) {
      MeetingFacade.MeetingSummaryResult summaryResult =
          new MeetingFacade.MeetingSummaryResult(
              result.joinedMeeting().summary(), result.meetingTagInfo());
      return new JoinedMeetingResponse(
          result.joinedMeeting().member().role().name(),
          MeetingSummaryResponse.from(summaryResult));
    }
  }
}
