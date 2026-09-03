package org.sopt.makers.storage.db.playground.post.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.post.port.PostReportRepositoryPort;
import org.sopt.makers.domain.playground.post.report.PostReport;
import org.sopt.makers.storage.db.playground.post.entity.PostReportEntity;
import org.sopt.makers.storage.db.playground.post.repository.PostReportJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostReportRepositoryAdapter implements PostReportRepositoryPort {

  private final PostReportJpaRepository repository;

  @Override
  public PostReport save(PostReport report) {
    return repository.save(PostReportEntity.fromDomain(report)).toDomain();
  }

  @Override
  public boolean existsByPostIdAndReporterId(Long postId, Long reporterId) {
    return repository.existsByPostIdAndReporterId(postId, reporterId);
  }
}
