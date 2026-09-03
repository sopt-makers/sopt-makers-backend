package org.sopt.makers.domain.playground.post.report;

import java.time.LocalDateTime;

public record PostReport(
    Long id, Long postId, Long reporterId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static PostReport create(Long postId, Long reporterId) {
    return new PostReport(null, postId, reporterId, null, null);
  }
}
