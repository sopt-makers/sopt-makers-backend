package org.sopt.makers.api.controller.crew.flash.dto;

import org.sopt.makers.api.controller.crew.meeting.dto.MeetingMapper;
import org.sopt.makers.domain.crew.flash.FlashPlaceType;
import org.sopt.makers.domain.crew.flash.FlashTimingType;
import org.sopt.makers.domain.crew.flash.facade.FlashFacade;

public final class FlashMapper {

  private FlashMapper() {}

  public static FlashFacade.FlashCommandValues toCommandValues(FlashBodyRequest request) {
    return new FlashFacade.FlashCommandValues(
        request.title(),
        request.desc(),
        FlashTimingType.ofValue(request.flashTimingType()),
        MeetingMapper.getStartDate(request.activityStartDate()),
        MeetingMapper.getEndDate(request.activityEndDate()),
        FlashPlaceType.ofValue(request.flashPlaceType()),
        request.flashPlace(),
        request.minimumCapacity(),
        request.maximumCapacity(),
        MeetingMapper.getImageURL(request.files()));
  }
}
