package org.sopt.makers.storage.db.crew.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.soptmap.EventGift;
import org.sopt.makers.domain.crew.soptmap.port.EventGiftRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.EventGiftEntity;
import org.sopt.makers.storage.db.crew.repository.EventGiftJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventGiftRepositoryAdapter implements EventGiftRepositoryPort {

  private final EventGiftJpaRepository repository;

  @Override
  @Transactional
  public EventGift save(EventGift eventGift) {
    return repository.save(EventGiftEntity.fromDomain(eventGift)).toDomain();
  }

  @Override
  public boolean existsByUserId(Long userId) {
    return repository.existsByUserId(userId);
  }

  @Override
  public Optional<EventGift> findFirstClaimableForUpdate() {
    return repository
        .findFirstByClaimableTrueAndActiveTrueOrderByIdAsc()
        .map(EventGiftEntity::toDomain);
  }

  @Override
  public Optional<EventGift> findActiveByUserIdAndMapId(Long userId, Long mapId) {
    return repository
        .findFirstByUserIdAndMapIdAndActiveTrue(userId, mapId)
        .map(EventGiftEntity::toDomain);
  }
}
