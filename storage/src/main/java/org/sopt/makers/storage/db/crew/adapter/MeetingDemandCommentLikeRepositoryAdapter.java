package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandCommentLike;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentLikeRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandCommentLikeEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingDemandCommentLikeJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingDemandCommentLikeRepositoryAdapter
    implements MeetingDemandCommentLikeRepositoryPort {

  private final MeetingDemandCommentLikeJpaRepository repository;

  @Override
  @Transactional
  public MeetingDemandCommentLike save(MeetingDemandCommentLike like) {
    return repository.save(MeetingDemandCommentLikeEntity.fromDomain(like)).toDomain();
  }

  @Override
  public boolean existsByCommentIdAndUserId(Long commentId, Long userId) {
    return repository.existsByMeetingDemandCommentIdAndUserId(commentId, userId);
  }

  @Override
  @Transactional
  public void deleteByCommentIdAndUserId(Long commentId, Long userId) {
    repository.deleteByMeetingDemandCommentIdAndUserId(commentId, userId);
  }

  @Override
  @Transactional
  public void deleteAllByCommentId(Long commentId) {
    repository.deleteAllByMeetingDemandCommentId(commentId);
  }

  @Override
  @Transactional
  public void deleteAllByCommentIds(List<Long> commentIds) {
    if (commentIds != null && !commentIds.isEmpty()) {
      repository.deleteAllByMeetingDemandCommentIdIn(commentIds);
    }
  }

  @Override
  public Set<Long> findLikedCommentIds(List<Long> commentIds, Long userId) {
    if (commentIds == null || commentIds.isEmpty()) {
      return Set.of();
    }
    return repository.findAllByMeetingDemandCommentIdInAndUserId(commentIds, userId).stream()
        .map(MeetingDemandCommentLikeEntity::getMeetingDemandCommentId)
        .collect(Collectors.toSet());
  }
}
