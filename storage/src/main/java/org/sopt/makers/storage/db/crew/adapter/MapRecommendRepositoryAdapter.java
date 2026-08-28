package org.sopt.makers.storage.db.crew.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.soptmap.MapRecommend;
import org.sopt.makers.domain.crew.soptmap.port.MapRecommendRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MapRecommendEntity;
import org.sopt.makers.storage.db.crew.repository.MapRecommendJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MapRecommendRepositoryAdapter implements MapRecommendRepositoryPort {

  private final MapRecommendJpaRepository repository;

  @Override
  @Transactional
  public MapRecommend save(MapRecommend recommend) {
    return repository.save(MapRecommendEntity.fromDomain(recommend)).toDomain();
  }

  @Override
  public Optional<MapRecommend> findByUserIdAndSoptMapId(Long userId, Long soptMapId) {
    return repository.findByUserIdAndSoptMapId(userId, soptMapId).map(MapRecommendEntity::toDomain);
  }

  @Override
  @Transactional
  public void deleteAllBySoptMapId(Long soptMapId) {
    repository.deleteAllBySoptMapId(soptMapId);
  }
}
