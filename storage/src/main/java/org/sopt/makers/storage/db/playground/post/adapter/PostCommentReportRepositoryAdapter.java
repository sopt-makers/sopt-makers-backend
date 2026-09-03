package org.sopt.makers.storage.db.playground.post.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.post.port.PostCommentReportRepositoryPort;
import org.sopt.makers.domain.playground.post.report.PostCommentReport;
import org.sopt.makers.storage.db.playground.post.entity.PostCommentReportEntity;
import org.sopt.makers.storage.db.playground.post.repository.PostCommentReportJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostCommentReportRepositoryAdapter implements PostCommentReportRepositoryPort {

  private final PostCommentReportJpaRepository repository;

  @Override
  public PostCommentReport save(PostCommentReport report) {
    return repository.save(PostCommentReportEntity.fromDomain(report)).toDomain();
  }

  @Override
  public boolean existsByCommentIdAndReporterId(Long commentId, Long reporterId) {
    return repository.existsByCommentIdAndReporterId(commentId, reporterId);
  }
}
