package org.sopt.makers.clients.playground;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "external.playground")
public record PlaygroundProperty(@NotBlank String url, @NotBlank String token) {}
