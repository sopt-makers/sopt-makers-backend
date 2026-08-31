package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandComment;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandCommentService;

public record MeetingDemandCommentResponse(
    Long id,
    String contents,
    MeetingDemandCommentWriterResponse writer,
    LocalDateTime createdDate,
    int likeCount,
    Boolean isLiked,
    Boolean isMine,
    int order,
    List<MeetingDemandReplyResponse> replies,
    Boolean isBlockedComment) {

  public static MeetingDemandCommentResponse from(MeetingDemandCommentService.CommentView view) {
    MeetingDemandComment comment = view.comment();
    return new MeetingDemandCommentResponse(
        comment.id(),
        comment.contents(),
        MeetingDemandCommentWriterResponse.from(view.writer()),
        comment.createdAt(),
        comment.likeCount(),
        view.isLiked(),
        view.isMine(),
        comment.order(),
        view.replies().stream().map(MeetingDemandReplyResponse::from).toList(),
        view.isBlockedComment());
  }
}
