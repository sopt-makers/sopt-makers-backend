package org.sopt.makers.domain.app.home;

public record AppServiceEntryStatus(
    String serviceName,
    boolean displayAlarmBadge,
    String alarmBadge,
    String iconUrl,
    String deepLink) {

  public static AppServiceEntryStatus of(AppService appService, AppServiceBadgeInfo badgeInfo) {
    return new AppServiceEntryStatus(
        AppServiceName.of(appService.serviceName()).getExposedName(),
        badgeInfo.displayAlarmBadge(),
        badgeInfo.alarmBadge(),
        appService.iconUrl(),
        appService.deepLink());
  }

  public static AppServiceEntryStatus withoutBadge(AppService appService) {
    return of(appService, AppServiceBadgeInfo.createWithAllDisabled());
  }
}
