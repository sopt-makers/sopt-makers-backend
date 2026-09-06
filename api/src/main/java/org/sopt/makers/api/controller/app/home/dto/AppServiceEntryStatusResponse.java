package org.sopt.makers.api.controller.app.home.dto;

import org.sopt.makers.domain.app.home.AppServiceEntryStatus;

public record AppServiceEntryStatusResponse(
    String serviceName,
    boolean displayAlarmBadge,
    String alarmBadge,
    String iconUrl,
    String deepLink) {

  public static AppServiceEntryStatusResponse of(AppServiceEntryStatus status) {
    return new AppServiceEntryStatusResponse(
        status.serviceName(),
        status.displayAlarmBadge(),
        status.alarmBadge(),
        status.iconUrl(),
        status.deepLink());
  }
}
