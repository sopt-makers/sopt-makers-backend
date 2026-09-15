package org.sopt.makers.storage.db.playground.post.adapter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.post.like.PostLike;
import org.sopt.makers.domain.playground.post.port.PostLikeRepositoryPort;
import org.sopt.makers.storage.db.playground.post.entity.MeetingPostLikeEntity;
import org.sopt.makers.storage.db.playground.post.repository.MeetingPostLikeJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

// PostRepositoryAdapter와 동일한 사유로 meeting_post_like 테이블을 분리 사용한다(자세한 내용은
// PostRepositoryAdapter 클래스 주석 참고). TODO: 추후 Crew-Playground 스키마 통합 시 단일 테이블 병합 검토 예정.
@Repository("meetingPostLikeRepositoryAdapter")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeRepositoryAdapter implements PostLikeRepositoryPort {

  private final MeetingPostLikeJpaRepository repository;

  @Override
  @Transactional
  public PostLike save(PostLike like) {
    return repository.save(MeetingPostLikeEntity.fromDomain(like)).toDomain();
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
        .map(MeetingPostLikeEntity::getPostId)
        .collect(Collectors.toSet());
  }

  @Override
  @Transactional
  public void deleteAllByUserId(Long userId) {
    repository.deleteAllByUserId(userId);
  }
}
