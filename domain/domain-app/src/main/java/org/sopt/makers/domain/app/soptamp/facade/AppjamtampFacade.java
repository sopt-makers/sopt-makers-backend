package org.sopt.makers.domain.app.soptamp.facade;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamMissionList;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTeamSummary;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUserStatus;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.domain.app.soptamp.port.SoptampPointUpdaterPort;
import org.sopt.makers.domain.app.soptamp.port.SoptampUserQueryPort;
import org.sopt.makers.domain.app.soptamp.service.AppjamMissionService;
import org.sopt.makers.domain.app.soptamp.service.AppjamStampService;
import org.sopt.makers.domain.app.soptamp.service.AppjamUserService;
import org.sopt.makers.domain.app.soptamp.service.MissionService;
import org.sopt.makers.domain.app.soptamp.service.StampService;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.domain.app.soptamp.stamp.port.StampClapQueryPort;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.SoptampUserPort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AppjamtampFacade {

  private final SoptampUserQueryPort soptampUserQueryPort;
  private final SoptampUserPort soptampUserPort;
  private final SoptampPointUpdaterPort soptampPointUpdaterPort;
  private final StampService stampService;
  private final StampClapQueryPort stampClapQueryPort;
  private final AppjamUserService appjamUserService;
  private final AppjamStampService appjamStampService;
  private final AppjamMissionService appjamMissionService;
  private final MissionService missionService;

  public record AppjamtampView(
      Stamp stamp,
      Mission mission,
      String ownerNickname,
      String ownerProfileImage,
      AppjamTeamSummary teamSummary,
      boolean mine,
      int myClapCount) {}

  public record StampWithProfile(Stamp stamp, String ownerNickname, String ownerProfileImage) {}

  @Transactional(readOnly = true)
  public AppjamMissionList getTeamMissions(
      Long userId, @Nullable TeamNumber teamNumber, @Nullable Boolean isCompleted) {
    AppjamTeamSummary teamSummary = resolveTeamSummary(userId, teamNumber);
    AppjamUserStatus appjamUserStatus = appjamUserService.getAppjamUserStatus(userId);
    return AppjamMissionList.of(
        appjamUserStatus,
        teamSummary,
        appjamMissionService.getMissions(teamSummary.teamNumber(), isCompleted));
  }

  @Transactional
  public AppjamtampView getAppjamtamps(Long requestUserId, Long missionId, String nickname) {
    SoptampUser owner =
        soptampUserQueryPort
            .findByNickname(nickname)
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER));
    Long ownerUserId = owner.userId();
    String ownerProfileImage =
        soptampUserPort
            .findWithActivitiesById(ownerUserId)
            .map(User::profile)
            .map(profile -> profile.profileImage())
            .orElse("");
    AppjamTeamSummary teamSummary = appjamUserService.getTeamSummaryByUserId(ownerUserId);
    Stamp stamp = stampService.findStamp(missionId, ownerUserId);
    int requestUserClapCount = stampClapQueryPort.getUserClapCount(requestUserId, stamp.id());
    Mission mission = missionService.getById(missionId);
    stampService.increaseViewCount(stamp.id());

    return new AppjamtampView(
        stamp.withViewCount(stamp.viewCount() + 1),
        mission,
        owner.nickname(),
        ownerProfileImage,
        teamSummary,
        Objects.equals(requestUserId, ownerUserId),
        requestUserClapCount);
  }

  @Transactional
  public StampWithProfile uploadStamp(
      Long userId, Long missionId, String contents, String image, String activityDate) {
    AppjamUserStatus appjamUserStatus = appjamUserService.getAppjamUserStatus(userId);
    if (!appjamUserStatus.isAppjamJoined()) {
      throw new SoptampException(SoptampFailure.TEAM_FORBIDDEN);
    }
    appjamStampService.checkDuplicateStamp(appjamUserStatus.teamNumber(), missionId);
    Stamp stamp = appjamStampService.register(userId, missionId, contents, image, activityDate);
    Mission mission = missionService.getById(missionId);
    soptampPointUpdaterPort.addPointByLevel(userId, mission.level());
    SoptampUser soptampUser =
        soptampUserQueryPort
            .findByUserId(userId)
            .orElseThrow(() -> new SoptampException(SoptampFailure.NOT_FOUND_SOPTAMP_USER));
    String profileImage =
        soptampUserPort
            .findWithActivitiesById(userId)
            .map(User::profile)
            .map(profile -> profile.profileImage())
            .orElse("");
    return new StampWithProfile(stamp, soptampUser.nickname(), profileImage);
  }

  @Transactional(readOnly = true)
  public AppjamUserStatus getAppjamStatus(Long userId) {
    return appjamUserService.getAppjamUserStatus(userId);
  }

  private AppjamTeamSummary resolveTeamSummary(Long userId, TeamNumber teamNumber) {
    if (teamNumber != null) {
      return appjamUserService.getTeamSummaryByTeamNumber(teamNumber);
    }
    return appjamUserService.getTeamSummaryByUserId(userId);
  }
}
