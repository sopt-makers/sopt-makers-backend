package org.sopt.makers.storage.db.crew.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.crew.entity.MapRecommendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapRecommendJpaRepository extends JpaRepository<MapRecommendEntity, Long> {

  Optional<MapRecommendEntity> findByUserIdAndSoptMapId(Long userId, Long soptMapId);

  void deleteAllBySoptMapId(Long soptMapId);
}
