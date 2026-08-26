package org.sopt.makers.storage.db.crew.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingRepositoryAdapter implements MeetingRepositoryPort {

  private final MeetingJpaRepository meetingJpaRepository;

  @Transactional
  @Override
  public Meeting save(Meeting meeting) {
    return meetingJpaRepository.save(MeetingEntity.fromDomain(meeting)).toDomain();
  }

  @Override
  public Optional<Meeting> findById(Long meetingId) {
    return meetingJpaRepository.findById(meetingId).map(MeetingEntity::toDomain);
  }

  @Override
  public Page<Meeting> findAll(Pageable pageable) {
    return meetingJpaRepository.findAll(pageable).map(MeetingEntity::toDomain);
  }

  @Override
  public Page<Meeting> findAllByUserId(Long userId, Pageable pageable) {
    return meetingJpaRepository.findAllByUserId(userId, pageable).map(MeetingEntity::toDomain);
  }

  @Override
  public Page<Meeting> findAllByMeetingDemandId(Long meetingDemandId, Pageable pageable) {
    return meetingJpaRepository
        .findAllByMeetingDemandId(meetingDemandId, pageable)
        .map(MeetingEntity::toDomain);
  }

  @Override
  public long countByMeetingDemandId(Long meetingDemandId) {
    return meetingJpaRepository.countByMeetingDemandId(meetingDemandId);
  }

  @Transactional
  @Override
  public void clearMeetingDemandId(Long meetingDemandId) {
    meetingJpaRepository.clearMeetingDemandId(meetingDemandId);
  }

  @Transactional
  @Override
  public void delete(Meeting meeting) {
    meetingJpaRepository.delete(MeetingEntity.fromDomain(meeting));
  }
}
