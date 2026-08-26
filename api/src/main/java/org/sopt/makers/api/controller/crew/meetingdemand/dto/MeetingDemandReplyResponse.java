package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandComment;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandCommentService;

public record MeetingDemandReplyResponse(
    Long id,
    String contents,
    MeetingDemandCommentWriterResponse writer,
    LocalDateTime createdDate,
    int likeCount,
    Boolean isLiked,
    Boolean isMine,
    int order,
    Boolean isBlockedComment) {

  public static MeetingDemandReplyResponse from(MeetingDemandCommentService.ReplyView view) {
    MeetingDemandComment comment = view.comment();
    return new MeetingDemandReplyResponse(
        comment.id(),
        comment.contents(),
        MeetingDemandCommentWriterResponse.from(view.writer()),
        comment.createdAt(),
        comment.likeCount(),
        view.isLiked(),
        view.isMine(),
        comment.order(),
        view.isBlockedComment());
  }
}
