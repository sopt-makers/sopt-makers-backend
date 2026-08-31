package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordPreference;
import org.sopt.makers.domain.crew.meeting.tag.port.MeetingKeywordPreferenceRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingKeywordPreferenceEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingKeywordPreferenceJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingKeywordPreferenceRepositoryAdapter
    implements MeetingKeywordPreferenceRepositoryPort {

  private final MeetingKeywordPreferenceJpaRepository preferenceJpaRepository;

  @Override
  @Transactional
  public MeetingKeywordPreference save(MeetingKeywordPreference preference) {
    return preferenceJpaRepository
        .save(MeetingKeywordPreferenceEntity.fromDomain(preference))
        .toDomain();
  }

  @Override
  public Optional<MeetingKeywordPreference> findByUserId(Long userId) {
    return preferenceJpaRepository.findById(userId).map(MeetingKeywordPreferenceEntity::toDomain);
  }

  @Override
  public List<MeetingKeywordPreference> findAll() {
    return preferenceJpaRepository.findAll().stream()
        .map(MeetingKeywordPreferenceEntity::toDomain)
        .toList();
  }
}
