package org.sopt.makers.api.controller.crew.meeting.dto;

import jakarta.validation.constraints.NotNull;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record UpdateApplyStatusRequest(
    @NotNull(message = "신청 ID는 필수 입력 값입니다.") Long applyId,
    @NotNull(message = "신청 상태는 필수 입력 값입니다.") Integer status) {

  public MeetingService.UpdateApplyStatusCommand toCommand() {
    return new MeetingService.UpdateApplyStatusCommand(
        applyId, MeetingMapper.getApplyStatus(status));
  }
}
