package org.sopt.makers.domain.app.soptamp.stamp;

import java.time.LocalDateTime;
import java.util.List;

public record Stamp(
    Long id,
    String contents,
    List<String> images,
    Long userId,
    Long missionId,
    String activityDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int clapCount,
    int viewCount,
    Long version) {

  public Stamp {
    images = images == null ? List.of() : List.copyOf(images);
  }
}
