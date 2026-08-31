package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandComment;

public record CreateMeetingDemandCommentResponse(Long commentId) {

  public static CreateMeetingDemandCommentResponse from(MeetingDemandComment comment) {
    return new CreateMeetingDemandCommentResponse(comment.id());
  }
}
