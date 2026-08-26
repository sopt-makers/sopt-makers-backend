package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandCommentService;

public record UpdateMeetingDemandCommentResponse(
    Long commentId, String contents, String updatedDate) {

  public static UpdateMeetingDemandCommentResponse from(
      MeetingDemandCommentService.UpdatedComment updatedComment) {
    return new UpdateMeetingDemandCommentResponse(
        updatedComment.comment().id(),
        updatedComment.comment().contents(),
        String.valueOf(updatedComment.updatedAt()));
  }
}
