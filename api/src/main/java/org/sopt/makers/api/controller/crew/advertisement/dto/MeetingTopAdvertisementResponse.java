package org.sopt.makers.api.controller.crew.advertisement.dto;

import org.sopt.makers.domain.crew.advertisement.Advertisement;
import org.sopt.makers.domain.crew.advertisement.AdvertisementEventType;
import org.sopt.makers.domain.crew.advertisement.MeetingTopAdvertisement;

public record MeetingTopAdvertisementResponse(
    Boolean isDisplay,
    AdvertisementEventType eventType,
    Integer advertisementId,
    String desktopImageUrl,
    String mobileImageUrl,
    String calendarImageUrl,
    Title title,
    String subTitle,
    String bannerLink1,
    String bannerLink2) {

  private static final int FIXED_PAGE = 1;
  private static final String MEETING_LIST_PATH = "/list";
  private static final String MEETING_DETAIL_PATH = "/detail";

  public static MeetingTopAdvertisementResponse from(MeetingTopAdvertisement result) {
    Advertisement advertisement = result.advertisement();
    return new MeetingTopAdvertisementResponse(
        advertisement.display(),
        advertisement.eventType(),
        advertisement.id(),
        advertisement.desktopImageUrl(),
        advertisement.mobileImageUrl(),
        advertisement.calendarImageUrl(),
        Title.from(advertisement),
        advertisement.subTitle(),
        createBannerLink1(result),
        createBannerLink2(result.applicationMeetingId()));
  }

  public static MeetingTopAdvertisementResponse notDisplay() {
    return new MeetingTopAdvertisementResponse(
        false, null, null, null, null, null, null, null, null, null);
  }

  private static String createBannerLink1(MeetingTopAdvertisement result) {
    if (result.activeGeneration() == null) {
      return null;
    }
    return "%s?search=%d기+%s&page=%d"
        .formatted(
            MEETING_LIST_PATH,
            result.activeGeneration(),
            result.advertisement().eventType().getDisplayName(),
            FIXED_PAGE);
  }

  private static String createBannerLink2(Long meetingId) {
    return meetingId == null ? null : "%s?id=%d".formatted(MEETING_DETAIL_PATH, meetingId);
  }

  public record Title(String prefix, String highlight, String suffix) {

    public static Title from(Advertisement advertisement) {
      return new Title(
          advertisement.titlePrefix(), advertisement.titleHighlight(), advertisement.titleSuffix());
    }
  }
}
