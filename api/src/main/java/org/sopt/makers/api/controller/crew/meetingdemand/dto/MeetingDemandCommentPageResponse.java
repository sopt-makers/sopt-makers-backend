package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import java.util.List;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandCommentService;

public record MeetingDemandCommentPageResponse(
    List<MeetingDemandCommentResponse> comments, PageMetaResponse meta) {

  public static MeetingDemandCommentPageResponse from(
      PageResult<MeetingDemandCommentService.CommentView> page) {
    return new MeetingDemandCommentPageResponse(
        page.content().stream().map(MeetingDemandCommentResponse::from).toList(),
        PageMetaResponse.from(page));
  }
}
