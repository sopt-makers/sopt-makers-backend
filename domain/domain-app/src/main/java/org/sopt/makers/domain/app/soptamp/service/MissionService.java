package org.sopt.makers.domain.app.soptamp.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.mission.MissionCompleteness;
import org.sopt.makers.domain.app.soptamp.mission.port.MissionRepositoryPort;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

  private final MissionRepositoryPort missionRepositoryPort;
  private final StampRepositoryPort stampRepositoryPort;

  public Mission getById(Long missionId) {
    return missionRepositoryPort
        .findById(missionId)
        .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_MISSION));
  }

  @Transactional
  public Mission register(String title, Integer level, String image) {
    return missionRepositoryPort.save(new Mission(null, title, level, true, List.of(image)));
  }

  public List<MissionCompleteness> findAllWithCompleteness(Long userId) {
    Set<Long> completedMissionIds =
        stampRepositoryPort.findAllByUserId(userId).stream()
            .map(Stamp::missionId)
            .collect(Collectors.toSet());
    return getAllDisplayed().stream()
        .map(
            mission -> new MissionCompleteness(mission, completedMissionIds.contains(mission.id())))
        .toList();
  }

  public List<Mission> getAllDisplayed() {
    return missionRepositoryPort.findAllByDisplayOrderByLevelAscTitleAsc(true);
  }

  public List<Mission> getCompleted(Long userId) {
    List<Long> completedMissionIds =
        stampRepositoryPort.findAllByUserId(userId).stream().map(Stamp::missionId).toList();
    return missionRepositoryPort.findByIdsOrderByLevelAndTitle(completedMissionIds);
  }

  public List<Mission> getIncomplete(Long userId) {
    List<Mission> displayedMissions = missionRepositoryPort.findAllByDisplay(true);
    List<Long> completedMissionIds =
        stampRepositoryPort.findAllByUserId(userId).stream().map(Stamp::missionId).toList();
    List<Long> incompleteMissionIds =
        displayedMissions.stream()
            .map(Mission::id)
            .filter(id -> !completedMissionIds.contains(id))
            .toList();
    return missionRepositoryPort.findDisplayedByIdsOrderByLevelAndTitle(incompleteMissionIds);
  }
}
