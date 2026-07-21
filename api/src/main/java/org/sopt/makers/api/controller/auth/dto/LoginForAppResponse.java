package org.sopt.makers.api.controller.auth.dto;

import org.sopt.makers.domain.auth.facade.AuthFacade;

public record LoginForAppResponse(String accessToken, String refreshToken, boolean isFirstLogin) {

  public static LoginForAppResponse from(AuthFacade.LoginResult result) {
    return new LoginForAppResponse(
        result.accessToken(), result.refreshToken(), result.isFirstLogin());
  }
}
