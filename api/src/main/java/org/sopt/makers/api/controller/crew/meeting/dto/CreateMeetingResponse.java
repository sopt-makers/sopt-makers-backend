package org.sopt.makers.api.controller.crew.meeting.dto;

import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;

public record CreateMeetingResponse(Long meetingId, Long tagId) {

  public static CreateMeetingResponse from(MeetingFacade.CreatedMeeting result) {
    return new CreateMeetingResponse(result.meeting().id(), result.tagId());
  }
}
