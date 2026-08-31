package org.sopt.makers.storage.db.crew.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandWaitHistory;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandWaitHistoryRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandWaitHistoryEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingDemandWaitHistoryJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingDemandWaitHistoryRepositoryAdapter
    implements MeetingDemandWaitHistoryRepositoryPort {

  private final MeetingDemandWaitHistoryJpaRepository repository;

  @Override
  @Transactional
  public MeetingDemandWaitHistory save(MeetingDemandWaitHistory history) {
    return repository.save(MeetingDemandWaitHistoryEntity.fromDomain(history)).toDomain();
  }

  @Override
  public boolean existsByMeetingDemandIdAndUserId(Long meetingDemandId, Long userId) {
    return repository.existsByMeetingDemandIdAndUserId(meetingDemandId, userId);
  }

  @Override
  @Transactional
  public void deleteAllByMeetingDemandId(Long meetingDemandId) {
    repository.deleteAllByMeetingDemandId(meetingDemandId);
  }
}
