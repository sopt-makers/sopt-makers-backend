package org.sopt.makers.storage.db.crew.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.sopt.makers.storage.db.crew.entity.EventGiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface EventGiftJpaRepository extends JpaRepository<EventGiftEntity, Long> {

  boolean existsByUserId(Long userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<EventGiftEntity> findFirstByClaimableTrueAndActiveTrueOrderByIdAsc();

  Optional<EventGiftEntity> findFirstByUserIdAndMapIdAndActiveTrue(Long userId, Long mapId);
}
