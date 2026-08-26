package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandWait;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandWaitRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandWaitEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingDemandWaitJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingDemandWaitRepositoryAdapter implements MeetingDemandWaitRepositoryPort {

  private final MeetingDemandWaitJpaRepository repository;

  @Override
  @Transactional
  public MeetingDemandWait save(MeetingDemandWait wait) {
    return repository.save(MeetingDemandWaitEntity.fromDomain(wait)).toDomain();
  }

  @Override
  public boolean existsByMeetingDemandIdAndUserId(Long meetingDemandId, Long userId) {
    return repository.existsByMeetingDemandIdAndUserId(meetingDemandId, userId);
  }

  @Override
  @Transactional
  public void deleteByMeetingDemandIdAndUserId(Long meetingDemandId, Long userId) {
    repository.deleteByMeetingDemandIdAndUserId(meetingDemandId, userId);
  }

  @Override
  @Transactional
  public void deleteAllByMeetingDemandId(Long meetingDemandId) {
    repository.deleteAllByMeetingDemandId(meetingDemandId);
  }

  @Override
  public long countByMeetingDemandId(Long meetingDemandId) {
    return repository.countByMeetingDemandId(meetingDemandId);
  }

  @Override
  public Set<Long> findMeetingDemandIdsByUserIdAndMeetingDemandIds(
      Long userId, List<Long> meetingDemandIds) {
    if (meetingDemandIds == null || meetingDemandIds.isEmpty()) {
      return Set.of();
    }
    return repository.findAllByMeetingDemandIdInAndUserId(meetingDemandIds, userId).stream()
        .map(MeetingDemandWaitEntity::getMeetingDemandId)
        .collect(Collectors.toSet());
  }

  @Override
  public List<Long> findUserIdsByMeetingDemandId(Long meetingDemandId) {
    return repository.findAllByMeetingDemandId(meetingDemandId).stream()
        .map(MeetingDemandWaitEntity::getUserId)
        .toList();
  }
}
