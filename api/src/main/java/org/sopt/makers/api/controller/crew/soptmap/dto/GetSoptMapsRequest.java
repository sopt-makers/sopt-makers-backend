package org.sopt.makers.api.controller.crew.soptmap.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.sopt.makers.domain.crew.soptmap.MapTag;
import org.sopt.makers.domain.crew.soptmap.SoptMapSortType;

public record GetSoptMapsRequest(
    List<MapTag> categories,
    SoptMapSortType sortType,
    String stationKeyword,
    @Min(1) Integer page,
    @Min(1) @Max(50) Integer take) {

  public SoptMapSortType sortTypeOrDefault() {
    return sortType == null ? SoptMapSortType.LATEST : sortType;
  }

  public int pageOrDefault() {
    return page == null ? 1 : page;
  }

  public int takeOrDefault() {
    return take == null ? 12 : take;
  }
}
