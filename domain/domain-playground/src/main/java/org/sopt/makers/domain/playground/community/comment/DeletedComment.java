package org.sopt.makers.domain.playground.community.comment;

import java.time.LocalDateTime;

public record DeletedComment(
    Long id,
    String content,
    Long postId,
    Long writerId,
    Long parentCommentId,
    Boolean isBlindWriter,
    Boolean isReported,
    LocalDateTime deletedAt) {

  public static DeletedComment from(Comment comment) {
    return new DeletedComment(
        null,
        comment.content(),
        comment.postId(),
        comment.writerId(),
        comment.parentCommentId(),
        comment.isBlindWriter(),
        comment.isReported(),
        LocalDateTime.now());
  }
}
