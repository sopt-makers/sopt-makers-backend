package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingJpaRepository;
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
  public List<Meeting> findAll() {
    return meetingJpaRepository.findAll().stream().map(MeetingEntity::toDomain).toList();
  }

  @Override
  public List<Meeting> findAllByUserId(Long userId) {
    return meetingJpaRepository.findAllByUserId(userId).stream()
        .map(MeetingEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public void delete(Meeting meeting) {
    meetingJpaRepository.delete(MeetingEntity.fromDomain(meeting));
  }
}
