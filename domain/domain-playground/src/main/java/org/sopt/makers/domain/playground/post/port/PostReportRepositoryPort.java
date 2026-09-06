package org.sopt.makers.domain.playground.post.port;

import org.sopt.makers.domain.playground.post.report.PostReport;

public interface PostReportRepositoryPort {

  PostReport save(PostReport report);

  boolean existsByPostIdAndReporterId(Long postId, Long reporterId);
}
