package org.sopt.makers.api.controller.crew.meeting.dto;

import org.sopt.makers.domain.crew.meeting.MeetingApply;

public record ApplyMeetingResponse(Long applyId) {

  public static ApplyMeetingResponse from(MeetingApply apply) {
    return new ApplyMeetingResponse(apply.id());
  }
}
