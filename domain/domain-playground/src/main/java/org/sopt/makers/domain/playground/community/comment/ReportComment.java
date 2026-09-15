package org.sopt.makers.domain.playground.community.comment;

import java.time.LocalDateTime;

public record ReportComment(Long id, Long commentId, Long reporterId, LocalDateTime createdAt) {

  public static ReportComment create(Long commentId, Long reporterId) {
    return new ReportComment(null, commentId, reporterId, null);
  }
}
