package org.sopt.makers.domain.app.home;

public record AppServiceBadgeInfo(boolean displayAlarmBadge, String alarmBadge) {

  public static AppServiceBadgeInfo createWithEnabledDisplayAlarmBadge(String alarmBadge) {
    return new AppServiceBadgeInfo(true, alarmBadge);
  }

  public static AppServiceBadgeInfo createWithAllDisabled() {
    return new AppServiceBadgeInfo(false, "");
  }
}
