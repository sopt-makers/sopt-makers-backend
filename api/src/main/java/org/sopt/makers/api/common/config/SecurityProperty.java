package org.sopt.makers.api.common.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "security")
public record SecurityProperty(@Valid @NotNull Api api, @NotNull Jwt jwt) {

  public record Api(@NotEmpty Map<String, String> keys, @NotEmpty List<String> securedEndpoints) {}

  public record Jwt(@Valid @NotNull Secret secret) {
    public record Secret(
        @Valid @NotNull Expiration expiration,
        @NotNull String secretKey,
        @Valid @NotNull Issuer issuer) {
      public record Expiration(
          @NotNull Long accessTokenExpiration, @NotNull Long refreshTokenExpiration) {}

      public record Issuer(@NotNull String issuerName) {}
    }
  }
}
