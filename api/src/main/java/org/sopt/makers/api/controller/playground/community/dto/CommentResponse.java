package org.sopt.makers.api.controller.playground.community.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sopt.makers.domain.playground.community.comment.Comment;
import org.sopt.makers.domain.playground.community.comment.CommentThread;

public record CommentResponse(
    Long id,
    MemberResponse member,
    Boolean isMine,
    Long postId,
    Long parentCommentId,
    String content,
    Boolean isBlindWriter,
    AnonymousProfileResponse anonymousProfile,
    Boolean isReported,
    LocalDateTime createdAt,
    Boolean isDeleted,
    Boolean isLiked,
    Integer likeCount,
    List<CommentResponse> replies) {

  public static CommentResponse from(CommentThread thread) {
    Comment comment = thread.comment();

    if (Boolean.TRUE.equals(comment.isDeleted())) {
      return new CommentResponse(
          comment.id(),
          null,
          null,
          comment.postId(),
          comment.parentCommentId(),
          null,
          null,
          null,
          null,
          null,
          true,
          null,
          null,
          new ArrayList<>());
    }

    boolean isBlind = Boolean.TRUE.equals(comment.isBlindWriter());

    return new CommentResponse(
        comment.id(),
        isBlind ? null : MemberResponse.from(thread.member()),
        thread.isMine(),
        comment.postId(),
        comment.parentCommentId(),
        comment.content(),
        comment.isBlindWriter(),
        isBlind ? AnonymousProfileResponse.from(thread.anonymousProfile()) : null,
        comment.isReported(),
        comment.createdAt(),
        false,
        thread.isLiked(),
        thread.likeCount(),
        new ArrayList<>());
  }

  /** 최상위 댓글 아래에 답글을 중첩시킨 트리 구조로 재구성한다. */
  public static List<CommentResponse> buildHierarchy(List<CommentResponse> flatComments) {
    Map<Long, CommentResponse> commentMap = new HashMap<>();
    List<CommentResponse> topLevelComments = new ArrayList<>();

    for (CommentResponse comment : flatComments) {
      commentMap.put(comment.id(), comment);
    }

    for (CommentResponse comment : flatComments) {
      if (comment.parentCommentId() == null) {
        topLevelComments.add(comment);
      } else {
        CommentResponse parent = commentMap.get(comment.parentCommentId());
        if (parent != null) {
          parent.replies().add(comment);
        }
      }
    }

    return topLevelComments;
  }
}
