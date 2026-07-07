package org.sopt.makers.api.controller.auth.dto;

import org.sopt.makers.domain.auth.port.TokenIssuerPort;

public record RefreshForWebResponse(String accessToken) {

  public static RefreshForWebResponse from(TokenIssuerPort.TokenPair tokenPair) {
    return new RefreshForWebResponse(tokenPair.accessToken());
  }
}
