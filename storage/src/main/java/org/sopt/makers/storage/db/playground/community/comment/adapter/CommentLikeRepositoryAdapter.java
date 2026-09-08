package org.sopt.makers.storage.db.playground.community.comment.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.comment.CommentLike;
import org.sopt.makers.domain.playground.community.comment.port.CommentLikeRepositoryPort;
import org.sopt.makers.storage.db.playground.community.comment.entity.CommentLikeEntity;
import org.sopt.makers.storage.db.playground.community.comment.repository.CommentLikeJpaRepository;
import org.sopt.makers.storage.db.playground.community.comment.repository.CommentLikeJpaRepository.CommentLikeCountProjection;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentLikeRepositoryAdapter implements CommentLikeRepositoryPort {

  private final CommentLikeJpaRepository commentLikeJpaRepository;

  @Transactional
  @Override
  public CommentLike save(CommentLike commentLike) {
    return commentLikeJpaRepository.save(CommentLikeEntity.from(commentLike)).toDomain();
  }

  @Transactional
  @Override
  public void delete(CommentLike commentLike) {
    commentLikeJpaRepository.deleteById(commentLike.id());
  }

  @Override
  public boolean existsByUserIdAndCommentId(Long userId, Long commentId) {
    return commentLikeJpaRepository.existsByUserIdAndCommentId(userId, commentId);
  }

  @Override
  public Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId) {
    return commentLikeJpaRepository.findByUserIdAndCommentId(userId, commentId).map(CommentLikeEntity::toDomain);
  }

  @Override
  public int countAllByCommentId(Long commentId) {
    return commentLikeJpaRepository.countAllByCommentId(commentId);
  }

  @Override
  public List<Long> findLikedCommentIdsByUserIdAndCommentIds(Long userId, List<Long> commentIds) {
    return commentLikeJpaRepository.findLikedCommentIdsByUserIdAndCommentIds(userId, commentIds);
  }

  @Override
  public Map<Long, Long> countLikesByCommentIds(List<Long> commentIds) {
    Map<Long, Long> result = new LinkedHashMap<>();
    for (CommentLikeCountProjection projection : commentLikeJpaRepository.countLikesByCommentIds(commentIds)) {
      result.put(projection.getCommentId(), projection.getLikeCount());
    }
    return result;
  }

  @Transactional
  @Override
  public void deleteAllByCommentIds(List<Long> commentIds) {
    commentLikeJpaRepository.deleteAllByCommentIdIn(commentIds);
  }
}
