package org.sopt.makers.clients.crew;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "external.crew")
public record CrewProperty(@NotBlank String url) {}
