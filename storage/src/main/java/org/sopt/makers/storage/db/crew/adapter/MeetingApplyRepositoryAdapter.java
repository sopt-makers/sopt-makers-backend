package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.MeetingApplyStatus;
import org.sopt.makers.domain.crew.meeting.port.MeetingApplyRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingApplyEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingApplyJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingApplyRepositoryAdapter implements MeetingApplyRepositoryPort {

  private final MeetingApplyJpaRepository meetingApplyJpaRepository;

  @Transactional
  @Override
  public MeetingApply save(MeetingApply apply) {
    return meetingApplyJpaRepository.save(MeetingApplyEntity.fromDomain(apply)).toDomain();
  }

  @Override
  public Optional<MeetingApply> findById(Long applyId) {
    return meetingApplyJpaRepository.findById(applyId).map(MeetingApplyEntity::toDomain);
  }

  @Override
  public List<MeetingApply> findAllByMeetingId(Long meetingId) {
    return meetingApplyJpaRepository.findAllByMeetingId(meetingId).stream()
        .map(MeetingApplyEntity::toDomain)
        .toList();
  }

  @Override
  public List<MeetingApply> findAllByMeetingIdAndStatus(Long meetingId, MeetingApplyStatus status) {
    return meetingApplyJpaRepository.findAllByMeetingIdAndStatus(meetingId, status).stream()
        .map(MeetingApplyEntity::toDomain)
        .toList();
  }

  @Override
  public List<MeetingApply> findAllByMeetingIdAndStatuses(
      Long meetingId, List<MeetingApplyStatus> statuses) {
    return meetingApplyJpaRepository.findAllByMeetingIdAndStatusIn(meetingId, statuses).stream()
        .map(MeetingApplyEntity::toDomain)
        .toList();
  }

  @Override
  public List<MeetingApply> findAllByUserIdAndStatus(Long userId, MeetingApplyStatus status) {
    return meetingApplyJpaRepository.findAllByUserIdAndStatus(userId, status).stream()
        .map(MeetingApplyEntity::toDomain)
        .toList();
  }

  @Override
  public boolean existsByMeetingIdAndUserId(Long meetingId, Long userId) {
    return meetingApplyJpaRepository.existsByMeetingIdAndUserId(meetingId, userId);
  }

  @Transactional
  @Override
  public void deleteByMeetingIdAndUserId(Long meetingId, Long userId) {
    meetingApplyJpaRepository.deleteByMeetingIdAndUserId(meetingId, userId);
  }

  @Transactional
  @Override
  public void deleteAllByMeetingId(Long meetingId) {
    meetingApplyJpaRepository.deleteAllByMeetingId(meetingId);
  }
}
