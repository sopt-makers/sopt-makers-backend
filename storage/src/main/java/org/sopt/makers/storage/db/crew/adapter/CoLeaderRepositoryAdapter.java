package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.CoLeader;
import org.sopt.makers.domain.crew.meeting.port.CoLeaderRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.CoLeaderEntity;
import org.sopt.makers.storage.db.crew.repository.CoLeaderJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoLeaderRepositoryAdapter implements CoLeaderRepositoryPort {

  private final CoLeaderJpaRepository coLeaderJpaRepository;

  @Transactional
  @Override
  public List<CoLeader> saveAll(List<CoLeader> coLeaders) {
    List<CoLeaderEntity> entities = coLeaders.stream().map(CoLeaderEntity::fromDomain).toList();
    return coLeaderJpaRepository.saveAll(entities).stream().map(CoLeaderEntity::toDomain).toList();
  }

  @Override
  public List<CoLeader> findAllByMeetingId(Long meetingId) {
    return coLeaderJpaRepository.findAllByMeetingId(meetingId).stream()
        .map(CoLeaderEntity::toDomain)
        .toList();
  }

  @Override
  public List<CoLeader> findAllByUserId(Long userId) {
    return coLeaderJpaRepository.findAllByUserId(userId).stream()
        .map(CoLeaderEntity::toDomain)
        .toList();
  }

  @Override
  public boolean existsByMeetingIdAndUserId(Long meetingId, Long userId) {
    return coLeaderJpaRepository.existsByMeetingIdAndUserId(meetingId, userId);
  }

  @Transactional
  @Override
  public void deleteAllByMeetingId(Long meetingId) {
    coLeaderJpaRepository.deleteAllByMeetingId(meetingId);
  }
}
