package org.sopt.makers.storage.db.playground.post.adapter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.post.like.PostCommentLike;
import org.sopt.makers.domain.playground.post.port.PostCommentLikeRepositoryPort;
import org.sopt.makers.storage.db.playground.post.entity.PostCommentLikeEntity;
import org.sopt.makers.storage.db.playground.post.repository.PostCommentLikeJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentLikeRepositoryAdapter implements PostCommentLikeRepositoryPort {

  private final PostCommentLikeJpaRepository repository;

  @Override
  @Transactional
  public PostCommentLike save(PostCommentLike like) {
    return repository.save(PostCommentLikeEntity.fromDomain(like)).toDomain();
  }

  @Override
  public boolean existsByCommentIdAndUserId(Long commentId, Long userId) {
    return repository.existsByCommentIdAndUserId(commentId, userId);
  }

  @Override
  @Transactional
  public void deleteByCommentIdAndUserId(Long commentId, Long userId) {
    repository.deleteByCommentIdAndUserId(commentId, userId);
  }

  @Override
  public Set<Long> findLikedCommentIds(List<Long> commentIds, Long userId) {
    if (commentIds == null || commentIds.isEmpty()) {
      return Set.of();
    }
    return repository.findAllByCommentIdInAndUserId(commentIds, userId).stream()
        .map(PostCommentLikeEntity::getCommentId)
        .collect(Collectors.toSet());
  }

  @Override
  @Transactional
  public void deleteAllByUserId(Long userId) {
    repository.deleteAllByUserId(userId);
  }
}
