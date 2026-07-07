package org.sopt.makers.api.controller.auth.dto;

import org.sopt.makers.domain.auth.facade.AuthFacade;

public record LoginForWebResponse(String accessToken, boolean isFirstLogin) {

  public static LoginForWebResponse from(AuthFacade.LoginResult result) {
    return new LoginForWebResponse(result.accessToken(), result.isFirstLogin());
  }
}
