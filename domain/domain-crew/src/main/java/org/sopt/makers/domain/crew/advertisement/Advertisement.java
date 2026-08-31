package org.sopt.makers.domain.crew.advertisement;

import java.time.LocalDateTime;

public record Advertisement(
    Integer id,
    String desktopImageUrl,
    String mobileImageUrl,
    String advertisementLink,
    String calendarImageUrl,
    String titlePrefix,
    String titleHighlight,
    String titleSuffix,
    String subTitle,
    AdvertisementCategory category,
    Long priority,
    LocalDateTime startDate,
    LocalDateTime endDate,
    boolean sponsoredContent,
    boolean display,
    AdvertisementEventType eventType,
    AdvertisementTargetGeneration targetGeneration,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public Advertisement update(UpdateValues values) {
    return new Advertisement(
        id,
        values.desktopImageUrl() == null ? desktopImageUrl : values.desktopImageUrl(),
        values.mobileImageUrl() == null ? mobileImageUrl : values.mobileImageUrl(),
        advertisementLink,
        values.calendarImageUrl() == null ? calendarImageUrl : values.calendarImageUrl(),
        values.titlePrefix() == null ? titlePrefix : values.titlePrefix(),
        values.titleHighlight() == null ? titleHighlight : values.titleHighlight(),
        values.titleSuffix() == null ? titleSuffix : values.titleSuffix(),
        values.subTitle() == null ? subTitle : values.subTitle(),
        category,
        priority,
        values.startDate() == null ? startDate : values.startDate(),
        values.endDate() == null ? endDate : values.endDate(),
        sponsoredContent,
        values.display() == null ? display : values.display(),
        eventType,
        targetGeneration,
        createdAt,
        updatedAt);
  }

  public record UpdateValues(
      Boolean display,
      LocalDateTime startDate,
      LocalDateTime endDate,
      String desktopImageUrl,
      String mobileImageUrl,
      String calendarImageUrl,
      String titlePrefix,
      String titleHighlight,
      String titleSuffix,
      String subTitle) {

    public boolean hasUpdateField() {
      return display != null
          || startDate != null
          || endDate != null
          || desktopImageUrl != null
          || mobileImageUrl != null
          || calendarImageUrl != null
          || titlePrefix != null
          || titleHighlight != null
          || titleSuffix != null
          || subTitle != null;
    }
  }
}
