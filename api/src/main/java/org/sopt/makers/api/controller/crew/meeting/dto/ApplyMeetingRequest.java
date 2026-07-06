package org.sopt.makers.api.controller.crew.meeting.dto;

import jakarta.validation.constraints.NotNull;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record ApplyMeetingRequest(@NotNull Long meetingId, String content) {

  public MeetingService.ApplyMeetingCommand toCommand() {
    return new MeetingService.ApplyMeetingCommand(meetingId, content);
  }
}
