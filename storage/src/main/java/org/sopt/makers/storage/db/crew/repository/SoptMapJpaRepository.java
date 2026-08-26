package org.sopt.makers.storage.db.crew.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.storage.db.crew.entity.SoptMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptMapJpaRepository extends JpaRepository<SoptMapEntity, Long> {

  boolean existsByPlaceName(String placeName);

  boolean existsByCreatorId(Long creatorId);

  boolean existsByCreatorIdAndId(Long creatorId, Long id);

  List<SoptMapEntity> findAllByCreatedAtBetweenOrderByIdAsc(
      LocalDateTime startAt, LocalDateTime endAt);
}
