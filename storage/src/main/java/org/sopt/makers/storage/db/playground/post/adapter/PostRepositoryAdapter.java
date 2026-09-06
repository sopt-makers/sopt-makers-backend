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
import org.sopt.makers.storage.db.playground.post.entity.MeetingPostEntity;
import org.sopt.makers.storage.db.playground.post.repository.MeetingPostJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crew 모임 게시판(무무) 전용 저장소. Community 자유게시판(domain-playground/community/post의 PostEntity,
 * "community_post" 테이블)과는 물리적으로 분리된 "meeting_post" 테이블을 사용한다 — 레거시 Playground 원본
 * 컬럼(anonymous_profile_id, sopticle_url, is_question 등) 유실 및 Community API 계약 파기를 막기 위한
 * 확정 구조다. TODO: 추후 Crew-Playground 스키마 통합 시 단일 테이블 병합 검토 예정.
 */
@Repository("meetingPostRepositoryAdapter")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostRepositoryAdapter implements PostRepositoryPort {

  private static final Sort LATEST = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

  private final MeetingPostJpaRepository repository;

  @Override
  @Transactional
  public Post save(Post post) {
    return repository.save(MeetingPostEntity.fromDomain(post)).toDomain();
  }

  @Override
  public Optional<Post> findById(Long postId) {
    return repository.findById(postId).map(MeetingPostEntity::toDomain);
  }

  @Override
  @Transactional
  public Optional<Post> findByIdForUpdate(Long postId) {
    return repository.findByIdForUpdate(postId).map(MeetingPostEntity::toDomain);
  }

  @Override
  public PageResult<Post> findByMeetingId(Long meetingId, PageQuery pageQuery) {
    return PageMapper.toPageResult(
        repository.findAllByMeetingId(meetingId, PageMapper.toPageable(pageQuery, LATEST)),
        MeetingPostEntity::toDomain);
  }

  @Override
  public PageResult<Post> findByMeetingIds(List<Long> meetingIds, PageQuery pageQuery) {
    if (meetingIds == null || meetingIds.isEmpty()) {
      return new PageResult<>(List.of(), 0, 0, pageQuery.page(), pageQuery.limit(), false, false);
    }
    return PageMapper.toPageResult(
        repository.findAllByMeetingIdIn(meetingIds, PageMapper.toPageable(pageQuery, LATEST)),
        MeetingPostEntity::toDomain);
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
        .map(MeetingPostEntity::toDomain)
        .toList();
  }

  @Override
  public long countByMeetingId(Long meetingId) {
    return repository.countByMeetingId(meetingId);
  }
}
