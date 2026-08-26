package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandCommentService;
import org.springframework.data.domain.Page;

public record MeetingDemandCommentPageResponse(
    List<MeetingDemandCommentResponse> comments, PageMetaResponse meta) {

  public static MeetingDemandCommentPageResponse from(
      Page<MeetingDemandCommentService.CommentView> page) {
    return new MeetingDemandCommentPageResponse(
        page.getContent().stream().map(MeetingDemandCommentResponse::from).toList(),
        PageMetaResponse.from(page));
  }
}
