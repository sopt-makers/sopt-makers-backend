package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.tag.MeetingTag;
import org.sopt.makers.domain.crew.meeting.tag.port.MeetingTagRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingTagEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingTagJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingTagRepositoryAdapter implements MeetingTagRepositoryPort {

  private final MeetingTagJpaRepository meetingTagJpaRepository;

  @Override
  @Transactional
  public MeetingTag save(MeetingTag meetingTag) {
    return meetingTagJpaRepository.save(MeetingTagEntity.fromDomain(meetingTag)).toDomain();
  }

  @Override
  public Optional<MeetingTag> findByMeetingId(Long meetingId) {
    return meetingTagJpaRepository.findByMeetingId(meetingId).map(MeetingTagEntity::toDomain);
  }

  @Override
  public Optional<MeetingTag> findByFlashId(Long flashId) {
    return meetingTagJpaRepository.findByFlashId(flashId).map(MeetingTagEntity::toDomain);
  }

  @Override
  public List<MeetingTag> findAllByMeetingIds(List<Long> meetingIds) {
    if (meetingIds == null || meetingIds.isEmpty()) {
      return List.of();
    }
    return meetingTagJpaRepository.findAllByMeetingIdIn(meetingIds).stream()
        .map(MeetingTagEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public void deleteByMeetingId(Long meetingId) {
    meetingTagJpaRepository.deleteByMeetingId(meetingId);
  }

  @Override
  @Transactional
  public void deleteByFlashId(Long flashId) {
    meetingTagJpaRepository.deleteByFlashId(flashId);
  }
}
