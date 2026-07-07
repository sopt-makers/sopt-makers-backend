package org.sopt.makers.api.controller.auth.dto;

import org.sopt.makers.domain.auth.port.TokenIssuerPort;

public record RefreshForAppResponse(String accessToken, String refreshToken) {

  public static RefreshForAppResponse from(TokenIssuerPort.TokenPair tokenPair) {
    return new RefreshForAppResponse(tokenPair.accessToken(), tokenPair.refreshToken());
  }
}
