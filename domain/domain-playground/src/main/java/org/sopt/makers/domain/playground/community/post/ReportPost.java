package org.sopt.makers.domain.playground.community.post;

import java.time.LocalDateTime;

public record ReportPost(Long id, Long postId, Long reporterId, LocalDateTime createdAt) {

  public static ReportPost create(Long postId, Long reporterId) {
    return new ReportPost(null, postId, reporterId, null);
  }
}
