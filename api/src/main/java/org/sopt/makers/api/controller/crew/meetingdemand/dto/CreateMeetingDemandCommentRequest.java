package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandCommentService;

public record CreateMeetingDemandCommentRequest(
    @NotBlank String contents, @NotNull Boolean isParent, Long parentCommentId) {

  @AssertTrue(message = "대댓글 작성 시 부모 댓글 id는 필수입니다.")
  public boolean isValidParentCommentId() {
    return Boolean.TRUE.equals(isParent) || parentCommentId != null;
  }

  public MeetingDemandCommentService.CreateCommentCommand toCommand() {
    return new MeetingDemandCommentService.CreateCommentCommand(
        contents, Boolean.TRUE.equals(isParent), parentCommentId);
  }
}
