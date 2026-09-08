package org.sopt.makers.storage.db.playground.community.post.adapter;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.post.PostLike;
import org.sopt.makers.domain.playground.community.post.port.PostLikeRepositoryPort;
import org.sopt.makers.storage.db.playground.community.post.entity.PostLikeEntity;
import org.sopt.makers.storage.db.playground.community.post.repository.PostLikeJpaRepository;
import org.sopt.makers.storage.db.playground.community.post.repository.PostLikeJpaRepository.PostLikeCountProjection;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeRepositoryAdapter implements PostLikeRepositoryPort {

  private final PostLikeJpaRepository postLikeJpaRepository;

  @Transactional
  @Override
  public PostLike save(PostLike postLike) {
    return postLikeJpaRepository.save(PostLikeEntity.from(postLike)).toDomain();
  }

  @Transactional
  @Override
  public void delete(PostLike postLike) {
    postLikeJpaRepository.deleteById(postLike.id());
  }

  @Transactional
  @Override
  public void deleteAllByPostId(Long postId) {
    postLikeJpaRepository.deleteAllByPostId(postId);
  }

  @Override
  public boolean existsByUserIdAndPostId(Long userId, Long postId) {
    return postLikeJpaRepository.existsByUserIdAndPostId(userId, postId);
  }

  @Override
  public Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId) {
    return postLikeJpaRepository
        .findByUserIdAndPostId(userId, postId)
        .map(PostLikeEntity::toDomain);
  }

  @Override
  public int countAllByPostId(Long postId) {
    return postLikeJpaRepository.countAllByPostId(postId);
  }

  @Override
  public int countAllByUserIdAndCreatedAtBetween(
      Long userId, LocalDateTime start, LocalDateTime end) {
    return postLikeJpaRepository.countAllByUserIdAndCreatedAtBetween(userId, start, end);
  }

  @Override
  public List<Long> findLikedPostIdsByUserIdAndPostIds(Long userId, List<Long> postIds) {
    return postLikeJpaRepository.findLikedPostIdsByUserIdAndPostIds(userId, postIds);
  }

  @Override
  public Map<Long, Long> countLikesByPostIds(List<Long> postIds) {
    Map<Long, Long> result = new LinkedHashMap<>();
    for (PostLikeCountProjection projection : postLikeJpaRepository.countLikesByPostIds(postIds)) {
      result.put(projection.getPostId(), projection.getLikeCount());
    }
    return result;
  }
}
