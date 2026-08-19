package org.sopt.makers.api.controller.crew.meeting.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.crew.meeting.MeetingApply;

public record MeetingApplyResponse(
    Long id,
    Integer type,
    Long meetingId,
    Long userId,
    String content,
    LocalDateTime appliedDate,
    Integer status) {

  public static MeetingApplyResponse from(MeetingApply apply) {
    return new MeetingApplyResponse(
        apply.id(),
        apply.type().getValue(),
        apply.meetingId(),
        apply.userId(),
        apply.content(),
        apply.appliedDate(),
        apply.status().getValue());
  }
}
