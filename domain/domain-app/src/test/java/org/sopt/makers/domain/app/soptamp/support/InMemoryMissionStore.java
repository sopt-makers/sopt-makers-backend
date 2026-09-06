package org.sopt.makers.domain.app.soptamp.support;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.mission.port.MissionRepositoryPort;

public final class InMemoryMissionStore implements MissionRepositoryPort {

  private final Map<Long, Mission> store = new LinkedHashMap<>();

  @Override
  public Optional<Mission> findById(Long missionId) {
    return Optional.ofNullable(store.get(missionId));
  }

  @Override
  public Mission save(Mission mission) {
    store.put(mission.id(), mission);
    return mission;
  }

  @Override
  public List<Mission> findAllByDisplay(boolean display) {
    return store.values().stream().filter(m -> m.display() == display).toList();
  }

  @Override
  public List<Mission> findAllByDisplayOrderByLevelAscTitleAsc(boolean display) {
    return findAllByDisplay(display);
  }

  @Override
  public List<Mission> findByIdsOrderByLevelAndTitle(List<Long> missionIds) {
    return missionIds.stream().map(store::get).filter(java.util.Objects::nonNull).toList();
  }

  @Override
  public List<Mission> findDisplayedByIdsOrderByLevelAndTitle(List<Long> missionIds) {
    return findByIdsOrderByLevelAndTitle(missionIds).stream().filter(Mission::display).toList();
  }

  @Override
  public void deleteAll() {
    store.clear();
  }
}
