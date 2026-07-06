package org.sopt.makers.api.controller.crew.meeting.dto;

import jakarta.validation.constraints.NotNull;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record ApplyMeetingRequest(
    @NotNull(message = "모임 ID는 필수 입력 값입니다.") Long meetingId, String content) {

  public MeetingService.ApplyMeetingCommand toCommand() {
    return new MeetingService.ApplyMeetingCommand(meetingId, content);
  }
}
