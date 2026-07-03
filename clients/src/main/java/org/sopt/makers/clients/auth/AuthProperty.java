package org.sopt.makers.clients.auth;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "external.auth")
public record AuthProperty(
    @NotBlank String url, @NotBlank String apiKey, @NotBlank String serviceName) {}
