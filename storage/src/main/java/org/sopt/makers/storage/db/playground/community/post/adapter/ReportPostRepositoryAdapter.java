package org.sopt.makers.storage.db.playground.community.post.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.post.ReportPost;
import org.sopt.makers.domain.playground.community.post.port.ReportPostRepositoryPort;
import org.sopt.makers.storage.db.playground.community.post.entity.ReportPostEntity;
import org.sopt.makers.storage.db.playground.community.post.repository.ReportPostJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportPostRepositoryAdapter implements ReportPostRepositoryPort {

  private final ReportPostJpaRepository reportPostJpaRepository;

  @Transactional
  @Override
  public ReportPost save(ReportPost reportPost) {
    return reportPostJpaRepository.save(ReportPostEntity.from(reportPost)).toDomain();
  }
}
