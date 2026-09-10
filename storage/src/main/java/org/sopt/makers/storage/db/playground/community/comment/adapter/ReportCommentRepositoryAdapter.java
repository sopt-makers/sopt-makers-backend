package org.sopt.makers.storage.db.playground.community.comment.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.comment.ReportComment;
import org.sopt.makers.domain.playground.community.comment.port.ReportCommentRepositoryPort;
import org.sopt.makers.storage.db.playground.community.comment.entity.ReportCommentEntity;
import org.sopt.makers.storage.db.playground.community.comment.repository.ReportCommentJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportCommentRepositoryAdapter implements ReportCommentRepositoryPort {

  private final ReportCommentJpaRepository reportCommentJpaRepository;

  @Transactional
  @Override
  public ReportComment save(ReportComment reportComment) {
    return reportCommentJpaRepository.save(ReportCommentEntity.from(reportComment)).toDomain();
  }

  @Transactional
  @Override
  public void deleteAllByCommentIds(List<Long> commentIds) {
    reportCommentJpaRepository.deleteAllByCommentIdIn(commentIds);
  }
}
