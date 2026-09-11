package org.sopt.makers.api.controller.app.home.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.home.AppServiceEntryStatus;

public record AppServiceEntryStatusResponse(
    @Schema(description = "앱 서비스 이름", example = "솝탬프") String serviceName,
    @Schema(description = "알림 뱃지를 띄울지 여부. 비로그인이면 항상 false", example = "true")
        boolean displayAlarmBadge,
    @Schema(description = "뱃지에 띄울 문구. 비로그인이면 빈 문자열", example = "N") String alarmBadge,
    @Schema(description = "서비스 아이콘 주소", example = "https://s3.sopt.org/icon/soptamp.png")
        String iconUrl,
    @Schema(description = "눌렀을 때 이동할 딥링크", example = "home/soptamp") String deepLink) {

  public static AppServiceEntryStatusResponse of(AppServiceEntryStatus status) {
    return new AppServiceEntryStatusResponse(
        status.serviceName(),
        status.displayAlarmBadge(),
        status.alarmBadge(),
        status.iconUrl(),
        status.deepLink());
  }
}
