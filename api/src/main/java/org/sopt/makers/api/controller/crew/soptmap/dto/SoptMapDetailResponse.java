package org.sopt.makers.api.controller.crew.soptmap.dto;

import java.util.List;
import org.sopt.makers.domain.crew.soptmap.MapTag;
import org.sopt.makers.domain.crew.soptmap.SoptMap;
import org.sopt.makers.domain.crew.soptmap.service.SoptMapService;

public record SoptMapDetailResponse(
    Long id,
    String placeName,
    String description,
    List<MapTag> tags,
    List<String> stationNames,
    long recommendCount,
    Boolean isRecommended,
    String kakaoLink,
    String naverLink,
    String creatorName,
    Boolean isCreator) {

  public static SoptMapDetailResponse from(SoptMapService.SoptMapView view) {
    SoptMap map = view.soptMap();
    return new SoptMapDetailResponse(
        map.id(),
        map.placeName(),
        map.description(),
        map.mapTags(),
        view.stationNames(),
        view.recommendCount(),
        view.isRecommended(),
        map.kakaoLink(),
        map.naverLink(),
        view.creatorName(),
        view.isCreator());
  }
}
