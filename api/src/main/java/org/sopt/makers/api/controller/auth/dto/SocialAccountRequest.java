package org.sopt.makers.api.controller.auth.dto;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class SocialAccountRequest {

  public record UpdateSocialAccount(
      @JsonProperty("phone") String phone,
      @JsonProperty("token") String idToken,
      @JsonProperty("authPlatform") String authPlatform) {}
}
