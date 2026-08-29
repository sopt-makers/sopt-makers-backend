package org.sopt.makers.api.controller.crew.advertisement.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.advertisement.Advertisement;

public record AdvertisementsResponse(List<AdvertisementResponse> advertisements) {

  public static AdvertisementsResponse from(List<Advertisement> advertisements) {
    return new AdvertisementsResponse(
        advertisements.stream().map(AdvertisementResponse::from).toList());
  }

  public record AdvertisementResponse(
      Integer advertisementId,
      String desktopImageUrl,
      String mobileImageUrl,
      String advertisementLink,
      LocalDateTime advertisementStartDate) {

    public static AdvertisementResponse from(Advertisement advertisement) {
      return new AdvertisementResponse(
          advertisement.id(),
          advertisement.desktopImageUrl(),
          advertisement.mobileImageUrl(),
          advertisement.advertisementLink(),
          advertisement.startDate());
    }
  }
}
