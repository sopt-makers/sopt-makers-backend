package org.sopt.makers.api.controller.crew.soptmap.dto;

import org.sopt.makers.domain.crew.soptmap.service.SoptMapService;

public record SoptMapGiftResponse(Long giftId, String giftUrl) {

  public static SoptMapGiftResponse from(SoptMapService.GiftResult result) {
    return new SoptMapGiftResponse(result.giftId(), result.giftUrl());
  }
}
