package org.sopt.makers.domain.app.soptamp.mission.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.mission.Mission;

public interface MissionRepositoryPort {

  Optional<Mission> findById(Long missionId);

  Mission save(Mission mission);

  List<Mission> findAllByDisplay(boolean display);

  List<Mission> findAllByDisplayOrderByLevelAscTitleAsc(boolean display);

  List<Mission> findByIdsOrderByLevelAndTitle(List<Long> missionIds);

  List<Mission> findDisplayedByIdsOrderByLevelAndTitle(List<Long> missionIds);

  void deleteAll();
}
