package org.sopt.makers.api.controller.auth.dto;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class AuthRequest {

  public record CreatePhoneVerification(
      @JsonProperty("userId") Long userId,
      @JsonProperty("phone") String phone,
      @JsonProperty("type") String verificationType) {}

  public record VerifyPhoneVerification(
      @JsonProperty("phone") String phone,
      @JsonProperty("code") String code,
      @JsonProperty("type") String verificationType) {}

  public record Login(
      @JsonProperty("token") String idToken, @JsonProperty("authPlatform") String authPlatform) {}

  public record SignUp(
      @JsonProperty("phone") String phone,
      @JsonProperty("token") String idToken,
      @JsonProperty("authPlatform") String authPlatform) {}

  public record TokenRefreshForApp(
      @JsonProperty("accessToken") String accessToken,
      @JsonProperty("refreshToken") String refreshToken) {}
}
