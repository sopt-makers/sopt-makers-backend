package org.sopt.makers.domain.app.soptamp.appjam;

public record AppjamTeamSummary(TeamNumber teamNumber, String teamName) {

  public static AppjamTeamSummary from(AppjamUser appjamUser) {
    return new AppjamTeamSummary(appjamUser.teamNumber(), appjamUser.teamName());
  }

  public static AppjamTeamSummary empty() {
    return new AppjamTeamSummary(null, null);
  }
}
