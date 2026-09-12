package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade.ClapResult;

public record AddClapResponse(
    @Schema(description = "박수를 친 스탬프 아이디", example = "1") Long stampId,
    @Schema(description = "이번 요청으로 실제 반영된 박수 수", example = "5") int appliedCount,
    @Schema(description = "스탬프가 받은 전체 박수 수", example = "42") int totalClapCount) {

  public static AddClapResponse of(Long stampId, ClapResult clapResult) {
    return new AddClapResponse(stampId, clapResult.appliedCount(), clapResult.totalClapCount());
  }
}
