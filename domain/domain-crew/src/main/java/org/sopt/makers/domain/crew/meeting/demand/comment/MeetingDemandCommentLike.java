package org.sopt.makers.domain.crew.meeting.demand.comment;

import java.time.LocalDateTime;

public record MeetingDemandCommentLike(
    Long id,
    Long meetingDemandCommentId,
    Long userId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static MeetingDemandCommentLike create(Long commentId, Long userId) {
    return new MeetingDemandCommentLike(null, commentId, userId, null, null);
  }
}
