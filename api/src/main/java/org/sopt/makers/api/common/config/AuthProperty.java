package org.sopt.makers.api.common.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "auth")
public record AuthProperty(@Valid @NotNull BypassLogin bypassLogin) {

  public record BypassLogin(@NotNull String phone, @NotNull String code, @NotNull String name) {}
}
