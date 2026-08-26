package org.sopt.makers.domain.crew.soptmap.service;

import static org.sopt.makers.domain.crew.soptmap.exception.SoptMapFailure.NOT_FOUND_SUBWAY_STATION;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.soptmap.SubwayStation;
import org.sopt.makers.domain.crew.soptmap.exception.SoptMapException;
import org.sopt.makers.domain.crew.soptmap.port.SubwayStationRepositoryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubwayStationService {

  private final SubwayStationRepositoryPort subwayStationRepositoryPort;

  public List<Long> resolveStationIds(List<String> stationNames) {
    if (stationNames == null || stationNames.isEmpty()) {
      return List.of();
    }
    List<String> uniqueNames = stationNames.stream().distinct().toList();
    List<SubwayStation> stations = subwayStationRepositoryPort.findAllByNames(uniqueNames);
    if (stations.size() != uniqueNames.size()) {
      throw new SoptMapException(NOT_FOUND_SUBWAY_STATION);
    }
    return stations.stream().map(SubwayStation::id).toList();
  }

  public List<SubwayStation> search(String keyword) {
    return subwayStationRepositoryPort.searchByKeyword(keyword);
  }

  public Map<Long, SubwayStation> getStationMap(List<Long> stationIds) {
    if (stationIds == null || stationIds.isEmpty()) {
      return Map.of();
    }
    return subwayStationRepositoryPort.findAllByIds(stationIds).stream()
        .collect(Collectors.toMap(SubwayStation::id, Function.identity()));
  }
}
