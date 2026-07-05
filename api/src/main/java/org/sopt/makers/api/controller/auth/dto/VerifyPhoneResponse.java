package org.sopt.makers.api.controller.auth.dto;

import org.sopt.makers.domain.auth.facade.AuthFacade;

public record VerifyPhoneResponse(String name, String phone) {

  public static VerifyPhoneResponse from(AuthFacade.VerifyResult result) {
    return new VerifyPhoneResponse(result.name(), result.phone());
  }
}
