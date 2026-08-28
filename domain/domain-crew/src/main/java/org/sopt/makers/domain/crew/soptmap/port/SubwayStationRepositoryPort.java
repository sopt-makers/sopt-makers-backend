package org.sopt.makers.domain.crew.soptmap.port;

import java.util.List;
import org.sopt.makers.domain.crew.soptmap.SubwayStation;

public interface SubwayStationRepositoryPort {

  List<SubwayStation> findAllByNames(List<String> stationNames);

  List<SubwayStation> findAllByIds(List<Long> stationIds);

  List<SubwayStation> searchByKeyword(String keyword);
}
