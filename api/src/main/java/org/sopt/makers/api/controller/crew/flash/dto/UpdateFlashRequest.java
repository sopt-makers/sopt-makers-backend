package org.sopt.makers.api.controller.crew.flash.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingTagMapper;
import org.sopt.makers.domain.crew.flash.facade.FlashFacade;
import org.sopt.makers.domain.crew.meeting.tag.WelcomeMessageType;

public record UpdateFlashRequest(
    @NotNull @Valid FlashBodyRequest flashBody,
    List<String> welcomeMessageTypes,
    @Size(min = 1, max = 2) List<String> meetingKeywordTypes) {

  public FlashFacade.UpdateFlashCommand toCommand() {
    List<WelcomeMessageType> welcomeMessages =
        MeetingTagMapper.toWelcomeMessageTypes(welcomeMessageTypes);
    return new FlashFacade.UpdateFlashCommand(
        FlashMapper.toCommandValues(flashBody),
        welcomeMessages == null ? List.of() : welcomeMessages,
        MeetingTagMapper.toMeetingKeywordTypes(meetingKeywordTypes));
  }
}
