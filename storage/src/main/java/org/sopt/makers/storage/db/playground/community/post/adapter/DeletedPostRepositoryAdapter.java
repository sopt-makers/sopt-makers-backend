package org.sopt.makers.storage.db.playground.community.post.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.post.DeletedPost;
import org.sopt.makers.domain.playground.community.post.port.DeletedPostRepositoryPort;
import org.sopt.makers.storage.db.playground.community.post.entity.DeletedPostEntity;
import org.sopt.makers.storage.db.playground.community.post.repository.DeletedPostJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeletedPostRepositoryAdapter implements DeletedPostRepositoryPort {

  private final DeletedPostJpaRepository deletedPostJpaRepository;

  @Transactional
  @Override
  public DeletedPost save(DeletedPost deletedPost) {
    return deletedPostJpaRepository.save(DeletedPostEntity.from(deletedPost)).toDomain();
  }
}
