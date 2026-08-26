package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandCommentProfile;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentProfileRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandCommentProfileEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingDemandCommentProfileJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingDemandCommentProfileRepositoryAdapter
    implements MeetingDemandCommentProfileRepositoryPort {

  private final MeetingDemandCommentProfileJpaRepository repository;

  @Override
  @Transactional
  public MeetingDemandCommentProfile save(MeetingDemandCommentProfile profile) {
    return repository.save(MeetingDemandCommentProfileEntity.fromDomain(profile)).toDomain();
  }

  @Override
  public Optional<MeetingDemandCommentProfile> findByMeetingDemandIdAndUserId(
      Long meetingDemandId, Long userId) {
    return repository
        .findByMeetingDemandIdAndUserId(meetingDemandId, userId)
        .map(MeetingDemandCommentProfileEntity::toDomain);
  }

  @Override
  public List<MeetingDemandCommentProfile> findAllByMeetingDemandIdAndUserIds(
      Long meetingDemandId, List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return List.of();
    }
    return repository.findAllByMeetingDemandIdAndUserIdIn(meetingDemandId, userIds).stream()
        .map(MeetingDemandCommentProfileEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public void deleteAllByMeetingDemandId(Long meetingDemandId) {
    repository.deleteAllByMeetingDemandId(meetingDemandId);
  }
}
