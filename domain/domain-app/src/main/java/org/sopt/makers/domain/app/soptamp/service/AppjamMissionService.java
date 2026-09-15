package org.sopt.makers.domain.app.soptamp.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamMission;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.appjam.port.AppjamUserRepositoryPort;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.mission.port.MissionRepositoryPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampRepositoryPort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppjamMissionService {

  private final AppjamUserRepositoryPort appjamUserRepositoryPort;
  private final MissionRepositoryPort missionRepositoryPort;
  private final StampRepositoryPort stampRepositoryPort;
  private final SoptampUserQueryPort soptampUserQueryPort;

  public List<AppjamMission> getMissions(
      @Nullable TeamNumber teamNumber, @Nullable Boolean completed) {
    List<AppjamMission> missions =
        teamNumber != null ? getMissionsByTeam(teamNumber) : getDisplayedMissions();

    if (completed == null) {
      return missions;
    }
    return missions.stream()
        .filter(mission -> Objects.equals(mission.isCompleted(), completed))
        .toList();
  }

  private List<AppjamMission> getDisplayedMissions() {
    return missionRepositoryPort.findAllByDisplayOrderByLevelAscTitleAsc(true).stream()
        .map(AppjamMission::createWhenUncompleted)
        .toList();
  }

  private List<AppjamMission> getMissionsByTeam(TeamNumber teamNumber) {
    List<Long> userIds = getTeamUserIds(teamNumber);
    Map<Long, Stamp> stampByMissionId = getStampMapByUserIds(userIds);
    Map<Long, SoptampUser> soptampUserByUserId = soptampUserQueryPort.findByUserIdsAsMap(userIds);

    return missionRepositoryPort.findAllByDisplayOrderByLevelAscTitleAsc(true).stream()
        .map(mission -> toTeamMission(mission, stampByMissionId, soptampUserByUserId))
        .toList();
  }

  private AppjamMission toTeamMission(
      Mission mission, Map<Long, Stamp> stampByMissionId, Map<Long, SoptampUser> usersById) {
    Optional<Stamp> stamp = Optional.ofNullable(stampByMissionId.get(mission.id()));
    String ownerName =
        stamp.map(Stamp::userId).map(usersById::get).map(SoptampUser::nickname).orElse(null);
    return AppjamMission.of(mission, stamp.isPresent(), ownerName);
  }

  private List<Long> getTeamUserIds(TeamNumber teamNumber) {
    return appjamUserRepositoryPort.findAllByTeamNumber(teamNumber).stream()
        .map(AppjamUser::userId)
        .toList();
  }

  private Map<Long, Stamp> getStampMapByUserIds(Collection<Long> userIds) {
    return stampRepositoryPort.findAllByUserIdIn(userIds).stream()
        .collect(
            Collectors.toMap(Stamp::missionId, Function.identity(), (exist, replace) -> exist));
  }
}
