package org.sopt.makers.api.controller.crew.flash.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingTagMapper;
import org.sopt.makers.domain.crew.flash.facade.FlashFacade;

public record CreateFlashRequest(
    @NotNull @Valid FlashBodyRequest flashBody,
    List<String> welcomeMessageTypes,
    @Size(min = 1, max = 2) List<String> meetingKeywordTypes) {

  public FlashFacade.CreateFlashCommand toCommand() {
    return new FlashFacade.CreateFlashCommand(
        FlashMapper.toCommandValues(flashBody),
        MeetingTagMapper.toWelcomeMessageTypes(welcomeMessageTypes),
        MeetingTagMapper.toMeetingKeywordTypes(meetingKeywordTypes));
  }
}
