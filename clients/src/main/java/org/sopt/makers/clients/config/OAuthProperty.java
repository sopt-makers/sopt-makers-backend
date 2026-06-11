package org.sopt.makers.clients.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "external.oauth")
public record OAuthProperty(@Valid @NotNull Apple apple, @Valid @NotNull Google google) {

  public record Apple(@NotNull String webAud, @NotNull String appAud) {}

  public record Google(@Valid @NotNull Client client) {
    public record Client(@NotNull String id) {}
  }
}
