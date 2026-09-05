package org.sopt.makers.storage.db.playground.post.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.PostContentType;
import org.sopt.makers.domain.playground.post.port.PostRepositoryPort;
import org.sopt.makers.storage.db.common.PageMapper;
import org.sopt.makers.storage.db.playground.post.entity.PostEntity;
import org.sopt.makers.storage.db.playground.post.repository.PostJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostRepositoryAdapter implements PostRepositoryPort {

  private static final Sort LATEST = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

  private final PostJpaRepository repository;

  @Override
  @Transactional
  public Post save(Post post) {
    return repository.save(PostEntity.fromDomain(post)).toDomain();
  }

  @Override
  public Optional<Post> findById(Long postId) {
    return repository.findById(postId).map(PostEntity::toDomain);
  }

  @Override
  @Transactional
  public Optional<Post> findByIdForUpdate(Long postId) {
    return repository.findByIdForUpdate(postId).map(PostEntity::toDomain);
  }

  @Override
  public PageResult<Post> findByMeetingId(Long meetingId, PageQuery pageQuery) {
    return PageMapper.toPageResult(
        repository.findAllByMeetingId(meetingId, PageMapper.toPageable(pageQuery, LATEST)),
        PostEntity::toDomain);
  }

  @Override
  public PageResult<Post> findByMeetingIds(List<Long> meetingIds, PageQuery pageQuery) {
    if (meetingIds == null || meetingIds.isEmpty()) {
      return new PageResult<>(List.of(), 0, 0, pageQuery.page(), pageQuery.limit(), false, false);
    }
    return PageMapper.toPageResult(
        repository.findAllByMeetingIdIn(meetingIds, PageMapper.toPageable(pageQuery, LATEST)),
        PostEntity::toDomain);
  }

  @Override
  public List<Post> findByMeetingIdsAndContentTypeAndCreatedAtBetweenExcludingWriter(
      List<Long> meetingIds,
      PostContentType contentType,
      LocalDateTime startAt,
      LocalDateTime endAt,
      Long writerId) {
    if (meetingIds == null || meetingIds.isEmpty()) {
      return List.of();
    }
    return repository
        .findAllByMeetingIdInAndContentTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThanAndWriterIdNotOrderByCreatedAtDesc(
            meetingIds, contentType, startAt, endAt, writerId)
        .stream()
        .map(PostEntity::toDomain)
        .toList();
  }

  @Override
  public long countByMeetingId(Long meetingId) {
    return repository.countByMeetingId(meetingId);
  }
}
