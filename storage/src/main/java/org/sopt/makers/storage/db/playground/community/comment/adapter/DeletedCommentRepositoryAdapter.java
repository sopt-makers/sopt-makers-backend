package org.sopt.makers.storage.db.playground.community.comment.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.comment.DeletedComment;
import org.sopt.makers.domain.playground.community.comment.port.DeletedCommentRepositoryPort;
import org.sopt.makers.storage.db.playground.community.comment.entity.DeletedCommentEntity;
import org.sopt.makers.storage.db.playground.community.comment.repository.DeletedCommentJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeletedCommentRepositoryAdapter implements DeletedCommentRepositoryPort {

  private final DeletedCommentJpaRepository deletedCommentJpaRepository;

  @Transactional
  @Override
  public DeletedComment save(DeletedComment deletedComment) {
    return deletedCommentJpaRepository.save(DeletedCommentEntity.from(deletedComment)).toDomain();
  }

  @Transactional
  @Override
  public List<DeletedComment> saveAll(List<DeletedComment> deletedComments) {
    return deletedCommentJpaRepository
        .saveAll(deletedComments.stream().map(DeletedCommentEntity::from).toList())
        .stream()
        .map(DeletedCommentEntity::toDomain)
        .toList();
  }
}
