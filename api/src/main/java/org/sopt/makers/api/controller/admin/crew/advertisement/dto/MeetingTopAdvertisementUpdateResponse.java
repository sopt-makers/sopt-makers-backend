package org.sopt.makers.api.controller.admin.crew.advertisement.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.crew.advertisement.Advertisement;

public record MeetingTopAdvertisementUpdateResponse(
    Integer advertisementId,
    Boolean isDisplay,
    LocalDateTime advertisementStartDate,
    LocalDateTime advertisementEndDate,
    String desktopImageUrl,
    String mobileImageUrl,
    String calendarImageUrl,
    String titlePrefix,
    String titleHighlight,
    String titleSuffix,
    String subTitle) {

  public static MeetingTopAdvertisementUpdateResponse from(Advertisement advertisement) {
    return new MeetingTopAdvertisementUpdateResponse(
        advertisement.id(),
        advertisement.display(),
        advertisement.startDate(),
        advertisement.endDate(),
        advertisement.desktopImageUrl(),
        advertisement.mobileImageUrl(),
        advertisement.calendarImageUrl(),
        advertisement.titlePrefix(),
        advertisement.titleHighlight(),
        advertisement.titleSuffix(),
        advertisement.subTitle());
  }
}
