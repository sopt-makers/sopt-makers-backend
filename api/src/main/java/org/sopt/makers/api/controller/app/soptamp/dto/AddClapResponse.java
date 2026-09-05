package org.sopt.makers.api.controller.app.soptamp.dto;

import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade.ClapResult;

public record AddClapResponse(Long stampId, int appliedCount, int totalClapCount) {

  public static AddClapResponse of(Long stampId, ClapResult clapResult) {
    return new AddClapResponse(stampId, clapResult.appliedCount(), clapResult.totalClapCount());
  }
}
