package org.sopt.makers.domain.playground.post.report;

import java.time.LocalDateTime;

public record PostCommentReport(
    Long id, Long commentId, Long reporterId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static PostCommentReport create(Long commentId, Long reporterId) {
    return new PostCommentReport(null, commentId, reporterId, null, null);
  }
}
