package org.sopt.makers.storage.db.playground.post.adapter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.post.like.PostLike;
import org.sopt.makers.domain.playground.post.port.PostLikeRepositoryPort;
import org.sopt.makers.storage.db.playground.post.entity.PostLikeEntity;
import org.sopt.makers.storage.db.playground.post.repository.PostLikeJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeRepositoryAdapter implements PostLikeRepositoryPort {

  private final PostLikeJpaRepository repository;

  @Override
  @Transactional
  public PostLike save(PostLike like) {
    return repository.save(PostLikeEntity.fromDomain(like)).toDomain();
  }

  @Override
  public boolean existsByPostIdAndUserId(Long postId, Long userId) {
    return repository.existsByPostIdAndUserId(postId, userId);
  }

  @Override
  @Transactional
  public void deleteByPostIdAndUserId(Long postId, Long userId) {
    repository.deleteByPostIdAndUserId(postId, userId);
  }

  @Override
  public Set<Long> findLikedPostIds(List<Long> postIds, Long userId) {
    if (postIds == null || postIds.isEmpty()) {
      return Set.of();
    }
    return repository.findAllByPostIdInAndUserId(postIds, userId).stream()
        .map(PostLikeEntity::getPostId)
        .collect(Collectors.toSet());
  }

  @Override
  @Transactional
  public void deleteAllByUserId(Long userId) {
    repository.deleteAllByUserId(userId);
  }
}
