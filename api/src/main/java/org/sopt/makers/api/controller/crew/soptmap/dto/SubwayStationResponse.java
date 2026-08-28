package org.sopt.makers.api.controller.crew.soptmap.dto;

import java.util.List;
import org.sopt.makers.domain.crew.soptmap.SubwayLine;
import org.sopt.makers.domain.crew.soptmap.SubwayStation;

public record SubwayStationResponse(Long id, String name, List<String> lines) {

  public static SubwayStationResponse from(SubwayStation station) {
    return new SubwayStationResponse(
        station.id(), station.name(), station.lines().stream().map(SubwayLine::getValue).toList());
  }

  public record ListResponse(List<SubwayStationResponse> stations) {}
}
