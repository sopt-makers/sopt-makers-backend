package org.sopt.makers.domain.playground.post.port;

import org.sopt.makers.domain.playground.post.report.PostCommentReport;

public interface PostCommentReportRepositoryPort {

  PostCommentReport save(PostCommentReport report);

  boolean existsByCommentIdAndReporterId(Long commentId, Long reporterId);
}
