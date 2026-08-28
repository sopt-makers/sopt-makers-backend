package org.sopt.makers.api.controller.admin.crew.advertisement.dto;

import java.time.LocalDateTime;
import org.sopt.makers.domain.crew.advertisement.Advertisement;

public record MeetingTopAdvertisementUpdateRequest(
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

  public Advertisement.UpdateValues toValues() {
    return new Advertisement.UpdateValues(
        isDisplay,
        advertisementStartDate,
        advertisementEndDate,
        desktopImageUrl,
        mobileImageUrl,
        calendarImageUrl,
        titlePrefix,
        titleHighlight,
        titleSuffix,
        subTitle);
  }
}
