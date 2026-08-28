package org.sopt.makers.api.controller.crew.advertisement;

import static org.sopt.makers.api.controller.crew.advertisement.AdvertisementSuccessCode.GET_ADVERTISEMENTS;
import static org.sopt.makers.api.controller.crew.advertisement.AdvertisementSuccessCode.GET_MEETING_TOP_ADVERTISEMENT;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.crew.advertisement.dto.AdvertisementsResponse;
import org.sopt.makers.api.controller.crew.advertisement.dto.MeetingTopAdvertisementResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.advertisement.AdvertisementCategory;
import org.sopt.makers.domain.crew.advertisement.AdvertisementEventType;
import org.sopt.makers.domain.crew.advertisement.service.AdvertisementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/advertisement/v2")
@RequiredArgsConstructor
public class AdvertisementController implements AdvertisementApi {

  private final AdvertisementService advertisementService;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getAdvertisements(
      @RequestParam AdvertisementCategory category) {
    return ResponseFactory.success(
        GET_ADVERTISEMENTS,
        AdvertisementsResponse.from(advertisementService.getGeneralAdvertisements(category)));
  }

  @Override
  @GetMapping("/meeting/top")
  public ResponseEntity<BaseResponse<?>> getMeetingTopAdvertisement(
      @RequestParam AdvertisementEventType eventType, @CurrentUserId Long userId) {
    MeetingTopAdvertisementResponse response =
        advertisementService
            .getMeetingTopAdvertisement(userId, eventType)
            .map(MeetingTopAdvertisementResponse::from)
            .orElseGet(MeetingTopAdvertisementResponse::notDisplay);
    return ResponseFactory.success(GET_MEETING_TOP_ADVERTISEMENT, response);
  }
}
