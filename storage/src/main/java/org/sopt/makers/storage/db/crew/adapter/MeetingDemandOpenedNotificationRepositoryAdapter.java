package org.sopt.makers.storage.db.crew.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandOpenedNotification;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandOpenedNotificationRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingDemandOpenedNotificationEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingDemandOpenedNotificationJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingDemandOpenedNotificationRepositoryAdapter
    implements MeetingDemandOpenedNotificationRepositoryPort {

  private final MeetingDemandOpenedNotificationJpaRepository repository;

  @Override
  @Transactional
  public MeetingDemandOpenedNotification save(MeetingDemandOpenedNotification notification) {
    return repository
        .save(MeetingDemandOpenedNotificationEntity.fromDomain(notification))
        .toDomain();
  }

  @Override
  public Optional<MeetingDemandOpenedNotification> findById(Long notificationId) {
    return repository.findById(notificationId).map(MeetingDemandOpenedNotificationEntity::toDomain);
  }

  @Override
  public Optional<MeetingDemandOpenedNotification> findByMeetingId(Long meetingId) {
    return repository
        .findByMeetingId(meetingId)
        .map(MeetingDemandOpenedNotificationEntity::toDomain);
  }

  @Override
  public List<Long> findPendingMeetingIds(LocalDateTime now) {
    return repository.findPendingMeetingIds(now);
  }
}
