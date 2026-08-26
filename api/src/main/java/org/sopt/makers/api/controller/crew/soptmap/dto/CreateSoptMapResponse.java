package org.sopt.makers.api.controller.crew.soptmap.dto;

import org.sopt.makers.domain.crew.soptmap.service.SoptMapService;

public record CreateSoptMapResponse(Long id, Boolean firstRegistered) {

  public static CreateSoptMapResponse from(SoptMapService.CreatedSoptMap result) {
    return new CreateSoptMapResponse(result.soptMapId(), result.firstRegistered());
  }

  public static CreateSoptMapResponse updated(Long id) {
    return new CreateSoptMapResponse(id, false);
  }
}
