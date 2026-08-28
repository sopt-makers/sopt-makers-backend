package org.sopt.makers.api.controller.crew.soptmap.dto;

import java.util.List;
import org.sopt.makers.domain.crew.soptmap.MapTag;
import org.sopt.makers.domain.crew.soptmap.SoptMap;
import org.sopt.makers.domain.crew.soptmap.service.SoptMapService;

public record SoptMapListItemResponse(
    Long id,
    String placeName,
    String description,
    List<MapTag> mapTags,
    List<String> subwayStationNames,
    long recommendCount,
    Boolean isRecommended,
    String kakaoLink,
    String naverLink,
    String creatorName,
    Boolean isCreator) {

  public static SoptMapListItemResponse from(SoptMapService.SoptMapView view) {
    SoptMap map = view.soptMap();
    return new SoptMapListItemResponse(
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
