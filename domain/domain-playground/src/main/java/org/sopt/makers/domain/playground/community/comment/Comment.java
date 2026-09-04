package org.sopt.makers.domain.playground.community.comment;

import java.time.LocalDateTime;

public record Comment(
    Long id,
    String content,
    Long postId,
    Long writerId,
    Long parentCommentId,
    Boolean isBlindWriter,
    Boolean isReported,
    Boolean isDeleted,
    Long anonymousProfileId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static Comment create(
      Long postId, Long writerId, Long parentCommentId, String content, Boolean isBlindWriter) {
    return new Comment(
        null, content, postId, writerId, parentCommentId, isBlindWriter, false, false, null, null, null);
  }

  public Comment withAnonymousProfileId(Long anonymousProfileId) {
    return new Comment(
        id,
        content,
        postId,
        writerId,
        parentCommentId,
        isBlindWriter,
        isReported,
        isDeleted,
        anonymousProfileId,
        createdAt,
        updatedAt);
  }

  public Comment withContent(String newContent) {
    return new Comment(
        id,
        newContent,
        postId,
        writerId,
        parentCommentId,
        isBlindWriter,
        isReported,
        isDeleted,
        anonymousProfileId,
        createdAt,
        updatedAt);
  }

  public Comment markAsDeleted() {
    return new Comment(
        id,
        content,
        postId,
        writerId,
        parentCommentId,
        isBlindWriter,
        isReported,
        true,
        anonymousProfileId,
        createdAt,
        updatedAt);
  }
}
