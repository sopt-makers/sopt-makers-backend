package org.sopt.makers.api.controller.crew.meeting.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record MeetingApplyWithUserResponse(
    Long id,
    Integer applyNumber,
    Integer type,
    Long meetingId,
    Long userId,
    String content,
    LocalDateTime appliedDate,
    Integer status,
    MeetingUserResponse user) {

  public static MeetingApplyWithUserResponse from(MeetingService.ApplyDetail detail) {
    MeetingApply apply = detail.apply();
    return new MeetingApplyWithUserResponse(
        apply.id(),
        null,
        apply.type().getValue(),
        apply.meetingId(),
        apply.userId(),
        apply.content(),
        apply.appliedDate(),
        apply.status().getValue(),
        MeetingUserResponse.from(detail.user()));
  }
}
