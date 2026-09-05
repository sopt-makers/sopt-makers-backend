package org.sopt.makers.domain.app.soptamp.appjam;

import java.util.List;

public record AppjamMissionList(
    TeamNumber myTeamNumber,
    boolean isAppjamJoined,
    TeamNumber teamNumber,
    String teamName,
    List<AppjamMission> missions) {

  public static AppjamMissionList of(
      AppjamUserStatus appjamUserStatus,
      AppjamTeamSummary teamSummary,
      List<AppjamMission> missions) {
    return new AppjamMissionList(
        appjamUserStatus.teamNumber(),
        appjamUserStatus.isAppjamJoined(),
        teamSummary.teamNumber(),
        teamSummary.teamName(),
        missions);
  }
}
