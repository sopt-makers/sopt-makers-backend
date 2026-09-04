package org.sopt.makers.storage.db.playground.community.comment.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.comment.Comment;
import org.sopt.makers.domain.playground.community.comment.port.CommentRepositoryPort;
import org.sopt.makers.storage.db.playground.community.comment.entity.CommentEntity;
import org.sopt.makers.storage.db.playground.community.comment.repository.CommentJpaRepository;
import org.sopt.makers.storage.db.playground.community.comment.repository.CommentJpaRepository.CommentCountProjection;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentRepositoryAdapter implements CommentRepositoryPort {

  private final CommentJpaRepository commentJpaRepository;

  @Transactional
  @Override
  public Comment save(Comment comment) {
    if (comment.id() == null) {
      return commentJpaRepository.save(CommentEntity.from(comment)).toDomain();
    }

    CommentEntity entity =
        commentJpaRepository
            .findById(comment.id())
            .orElseThrow(() -> new IllegalStateException("존재하지 않는 댓글입니다. id: " + comment.id()));
    entity.applyChanges(comment);
    return commentJpaRepository.saveAndFlush(entity).toDomain();
  }

  @Override
  public Optional<Comment> findById(Long commentId) {
    return commentJpaRepository.findById(commentId).map(CommentEntity::toDomain);
  }

  @Override
  public boolean existsById(Long commentId) {
    return commentJpaRepository.existsById(commentId);
  }

  @Override
  public List<Comment> findAllByPostId(Long postId) {
    return commentJpaRepository.findAllByPostIdOrderByIdAsc(postId).stream().map(CommentEntity::toDomain).toList();
  }

  @Override
  public List<Comment> findAllByPostIds(List<Long> postIds) {
    return commentJpaRepository.findAllByPostIdInOrderByPostIdAscIdAsc(postIds).stream()
        .map(CommentEntity::toDomain)
        .toList();
  }

  @Override
  public List<Comment> findAllByParentCommentId(Long parentCommentId) {
    return commentJpaRepository.findAllByParentCommentId(parentCommentId).stream().map(CommentEntity::toDomain).toList();
  }

  @Override
  public Map<Long, Long> countNonDeletedByPostIds(List<Long> postIds) {
    Map<Long, Long> result = new LinkedHashMap<>();
    for (CommentCountProjection projection : commentJpaRepository.countNonDeletedByPostIds(postIds)) {
      result.put(projection.getPostId(), projection.getCommentCount());
    }
    return result;
  }

  @Transactional
  @Override
  public void deleteAllByPostId(Long postId) {
    commentJpaRepository.deleteAllByPostId(postId);
  }
}
