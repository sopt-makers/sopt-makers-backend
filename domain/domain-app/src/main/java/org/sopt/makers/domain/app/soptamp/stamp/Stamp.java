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

  public static Stamp create(
      Long userId, Long missionId, String contents, String image, String activityDate) {
    return new Stamp(
        null, contents, List.of(image), userId, missionId, activityDate, null, null, 0, 0, null);
  }

  public Stamp withViewCount(int newViewCount) {
    return new Stamp(
        id,
        contents,
        images,
        userId,
        missionId,
        activityDate,
        createdAt,
        updatedAt,
        clapCount,
        newViewCount,
        version);
  }
}
