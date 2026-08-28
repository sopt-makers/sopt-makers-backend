package org.sopt.makers.domain.crew.soptmap.port;

import java.util.Optional;
import org.sopt.makers.domain.crew.soptmap.MapRecommend;

public interface MapRecommendRepositoryPort {

  MapRecommend save(MapRecommend recommend);

  Optional<MapRecommend> findByUserIdAndSoptMapId(Long userId, Long soptMapId);

  void deleteAllBySoptMapId(Long soptMapId);
}
