package org.sopt.makers.api.controller.auth.dto;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.auth.facade.AuthFacade;
import org.sopt.makers.domain.auth.port.TokenIssuerPort;

@NoArgsConstructor(access = PRIVATE)
public final class AuthResponse {

  public record VerifyPhoneResult(
      @JsonProperty("name") String name, @JsonProperty("phone") String phone) {
    public static VerifyPhoneResult from(AuthFacade.VerifyResult result) {
      return new VerifyPhoneResult(result.name(), result.phone());
    }
  }

  public record LoginForWebResult(
      @JsonProperty("accessToken") String accessToken,
      @JsonProperty("isFirstLogin") boolean isFirstLogin) {
    public static LoginForWebResult from(AuthFacade.LoginResult result) {
      return new LoginForWebResult(result.accessToken(), result.isFirstLogin());
    }
  }

  public record LoginForAppResult(
      @JsonProperty("accessToken") String accessToken,
      @JsonProperty("refreshToken") String refreshToken,
      @JsonProperty("isFirstLogin") boolean isFirstLogin) {
    public static LoginForAppResult from(AuthFacade.LoginResult result) {
      return new LoginForAppResult(
          result.accessToken(), result.refreshToken(), result.isFirstLogin());
    }
  }

  public record RefreshForWebResult(@JsonProperty("accessToken") String accessToken) {
    public static RefreshForWebResult from(TokenIssuerPort.TokenPair tokenPair) {
      return new RefreshForWebResult(tokenPair.accessToken());
    }
  }

  public record RefreshForAppResult(
      @JsonProperty("accessToken") String accessToken,
      @JsonProperty("refreshToken") String refreshToken) {
    public static RefreshForAppResult from(TokenIssuerPort.TokenPair tokenPair) {
      return new RefreshForAppResult(tokenPair.accessToken(), tokenPair.refreshToken());
    }
  }

  public record SocialAccountPlatform(@JsonProperty("platform") String platformName) {}
}
