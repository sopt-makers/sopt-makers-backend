package org.sopt.makers.domain.crew.soptmap;

import static org.sopt.makers.domain.crew.soptmap.exception.SoptMapFailure.FORBIDDEN_SOPT_MAP;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.sopt.makers.domain.crew.soptmap.exception.SoptMapException;

public record SoptMap(
    Long id,
    List<Long> nearbyStationIds,
    String placeName,
    String description,
    List<MapTag> mapTags,
    String naverLink,
    String kakaoLink,
    Long creatorId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public SoptMap {
    nearbyStationIds = nearbyStationIds == null ? List.of() : List.copyOf(nearbyStationIds);
    mapTags = mapTags == null ? List.of() : List.copyOf(mapTags);
  }

  public static SoptMap create(Long creatorId, Values values, List<Long> stationIds) {
    return new SoptMap(
        null,
        stationIds,
        values.placeName(),
        values.description(),
        values.mapTags(),
        values.naverLink(),
        values.kakaoLink(),
        creatorId,
        null,
        null);
  }

  public SoptMap update(Values values, List<Long> stationIds) {
    return new SoptMap(
        id,
        stationIds,
        values.placeName(),
        values.description(),
        values.mapTags(),
        values.naverLink(),
        values.kakaoLink(),
        creatorId,
        createdAt,
        updatedAt);
  }

  public void validateCreator(Long userId) {
    if (!Objects.equals(creatorId, userId)) {
      throw new SoptMapException(FORBIDDEN_SOPT_MAP);
    }
  }

  public record Values(
      String placeName,
      String description,
      List<MapTag> mapTags,
      String naverLink,
      String kakaoLink) {}
}
