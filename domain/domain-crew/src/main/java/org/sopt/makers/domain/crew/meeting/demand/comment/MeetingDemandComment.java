package org.sopt.makers.domain.crew.meeting.demand.comment;

import static org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandFailure.FORBIDDEN_MEETING_DEMAND_COMMENT;

import java.time.LocalDateTime;
import java.util.Objects;
import org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandException;

public record MeetingDemandComment(
    Long id,
    Long meetingDemandId,
    Long userId,
    String contents,
    int depth,
    int order,
    Long parentId,
    int likeCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  private static final int PARENT_DEPTH = 0;
  private static final int REPLY_DEPTH = 1;
  private static final String DELETED_CONTENT = "삭제된 댓글입니다.";

  public static MeetingDemandComment createParent(
      Long meetingDemandId, Long userId, String contents) {
    return new MeetingDemandComment(
        null, meetingDemandId, userId, contents, PARENT_DEPTH, 0, null, 0, null, null);
  }

  public static MeetingDemandComment createReply(
      Long meetingDemandId, Long userId, String contents, Long parentId, int order) {
    return new MeetingDemandComment(
        null, meetingDemandId, userId, contents, REPLY_DEPTH, order, parentId, 0, null, null);
  }

  public boolean isParent() {
    return depth == PARENT_DEPTH;
  }

  public boolean isReply() {
    return depth == REPLY_DEPTH;
  }

  public boolean isWriter(Long requestUserId) {
    return Objects.equals(userId, requestUserId);
  }

  public void validateWriter(Long requestUserId) {
    if (!isWriter(requestUserId)) {
      throw new MeetingDemandException(FORBIDDEN_MEETING_DEMAND_COMMENT);
    }
  }

  public MeetingDemandComment updateContents(String updatedContents) {
    return copy(userId, updatedContents, likeCount);
  }

  public MeetingDemandComment deleteParent() {
    return copy(null, DELETED_CONTENT, likeCount);
  }

  public MeetingDemandComment increaseLikeCount() {
    return copy(userId, contents, likeCount + 1);
  }

  public MeetingDemandComment decreaseLikeCount() {
    return copy(userId, contents, Math.max(0, likeCount - 1));
  }

  public MeetingDemandComment removeDeletedMention(String mentionName, Long mentionUserId) {
    if (mentionName == null || mentionUserId == null) {
      return this;
    }
    String token = "-~!@#@" + mentionName + "[" + mentionUserId + "]%^&*+";
    return updateContents(contents.replace(token, "@_"));
  }

  private MeetingDemandComment copy(Long nextUserId, String nextContents, int nextLikeCount) {
    return new MeetingDemandComment(
        id,
        meetingDemandId,
        nextUserId,
        nextContents,
        depth,
        order,
        parentId,
        nextLikeCount,
        createdAt,
        updatedAt);
  }
}
