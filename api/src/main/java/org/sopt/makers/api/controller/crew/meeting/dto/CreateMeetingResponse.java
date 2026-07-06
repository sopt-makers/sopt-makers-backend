package org.sopt.makers.api.controller.crew.meeting.dto;

import org.sopt.makers.domain.crew.meeting.Meeting;

public record CreateMeetingResponse(Long meetingId) {

  public static CreateMeetingResponse from(Meeting meeting) {
    return new CreateMeetingResponse(meeting.id());
  }
}
