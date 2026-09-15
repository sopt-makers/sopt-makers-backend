package org.sopt.makers.storage.db.playground.post.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.comment.PostComment;
import org.sopt.makers.domain.playground.post.port.PostCommentRepositoryPort;
import org.sopt.makers.storage.db.common.PageMapper;
import org.sopt.makers.storage.db.playground.post.entity.PostCommentEntity;
import org.sopt.makers.storage.db.playground.post.repository.PostCommentJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentRepositoryAdapter implements PostCommentRepositoryPort {

  private final PostCommentJpaRepository repository;

  @Override
  @Transactional
  public PostComment save(PostComment comment) {
    return repository.save(PostCommentEntity.fromDomain(comment)).toDomain();
  }

  @Override
  public Optional<PostComment> findById(Long commentId) {
    return repository.findById(commentId).map(PostCommentEntity::toDomain);
  }

  @Override
  @Transactional
  public Optional<PostComment> findByIdForUpdate(Long commentId) {
    return repository.findByIdForUpdate(commentId).map(PostCommentEntity::toDomain);
  }

  @Override
  public PageResult<PostComment> findParents(Long postId, PageQuery pageQuery) {
    Sort sort = Sort.by(Sort.Order.asc("createdAt"), Sort.Order.asc("id"));
    return PageMapper.toPageResult(
        repository.findAllByPostIdAndParentCommentIdIsNull(
            postId, PageMapper.toPageable(pageQuery, sort)),
        PostCommentEntity::toDomain);
  }

  @Override
  public List<PostComment> findReplies(List<Long> parentCommentIds) {
    if (parentCommentIds == null || parentCommentIds.isEmpty()) {
      return List.of();
    }
    return repository
        .findAllByParentCommentIdInOrderByParentCommentIdAscOrderAsc(parentCommentIds)
        .stream()
        .map(PostCommentEntity::toDomain)
        .toList();
  }

  @Override
  public int findMaxReplyOrder(Long parentCommentId) {
    return repository
        .findFirstByParentCommentIdOrderByOrderDesc(parentCommentId)
        .map(PostCommentEntity::getOrder)
        .orElse(0);
  }

  @Override
  public List<Long> findDistinctWriterIdsByPostId(Long postId) {
    return repository.findDistinctWriterIdsByPostId(postId);
  }
}
