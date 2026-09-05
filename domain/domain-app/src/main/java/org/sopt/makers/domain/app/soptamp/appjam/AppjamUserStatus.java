package org.sopt.makers.domain.app.soptamp.appjam;

public record AppjamUserStatus(TeamNumber teamNumber, String teamName, boolean isAppjamJoined) {

  public static AppjamUserStatus joined(AppjamUser appjamUser) {
    return new AppjamUserStatus(appjamUser.teamNumber(), appjamUser.teamName(), true);
  }

  public static AppjamUserStatus notJoined() {
    return new AppjamUserStatus(null, null, false);
  }
}
