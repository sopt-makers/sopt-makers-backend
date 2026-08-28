package org.sopt.makers.domain.crew.soptmap;

import java.time.LocalDateTime;

public record MapRecommend(
    Long id,
    Long userId,
    Long soptMapId,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static MapRecommend create(Long userId, Long soptMapId) {
    return new MapRecommend(null, userId, soptMapId, true, null, null);
  }

  public MapRecommend toggle() {
    return new MapRecommend(id, userId, soptMapId, !active, createdAt, updatedAt);
  }
}
