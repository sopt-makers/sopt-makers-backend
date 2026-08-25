package org.sopt.makers.storage.db.crew.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.flash.Flash;
import org.sopt.makers.domain.crew.flash.port.FlashRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.FlashEntity;
import org.sopt.makers.storage.db.crew.repository.FlashJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlashRepositoryAdapter implements FlashRepositoryPort {

  private final FlashJpaRepository flashJpaRepository;

  @Override
  @Transactional
  public Flash save(Flash flash) {
    return flashJpaRepository.save(FlashEntity.fromDomain(flash)).toDomain();
  }

  @Override
  public Optional<Flash> findByMeetingId(Long meetingId) {
    return flashJpaRepository.findByMeetingId(meetingId).map(FlashEntity::toDomain);
  }

  @Override
  @Transactional
  public void deleteByMeetingId(Long meetingId) {
    flashJpaRepository.deleteByMeetingId(meetingId);
  }
}
