package org.sopt.makers.api.controller.crew.flash.dto;

import org.sopt.makers.domain.crew.flash.facade.FlashFacade;

public record CreateFlashResponse(Long meetingId, Long tagId) {

  public static CreateFlashResponse from(FlashFacade.CreatedFlash result) {
    return new CreateFlashResponse(result.meetingId(), result.tagId());
  }
}
