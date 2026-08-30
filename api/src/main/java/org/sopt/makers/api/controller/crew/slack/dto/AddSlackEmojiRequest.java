package org.sopt.makers.api.controller.crew.slack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.sopt.makers.domain.crew.slack.service.SlackEmojiService;

public record AddSlackEmojiRequest(
    @NotBlank String identifiedPwd,
    @NotBlank String callEmoji,
    @NotBlank String username,
    @NotBlank String userSlackId,
    @NotBlank String team,
    @NotNull @Positive Integer generation,
    @NotBlank String templateCd) {

  public SlackEmojiService.AddMappingCommand toCommand() {
    return new SlackEmojiService.AddMappingCommand(
        callEmoji, username, userSlackId, team, generation, templateCd);
  }
}
