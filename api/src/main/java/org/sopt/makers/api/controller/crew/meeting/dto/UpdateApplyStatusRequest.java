package org.sopt.makers.api.controller.crew.meeting.dto;

import jakarta.validation.constraints.NotNull;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record UpdateApplyStatusRequest(@NotNull Long applyId, @NotNull Integer status) {

  public MeetingService.UpdateApplyStatusCommand toCommand() {
    return new MeetingService.UpdateApplyStatusCommand(
        applyId, MeetingMapper.getApplyStatus(status));
  }
}
