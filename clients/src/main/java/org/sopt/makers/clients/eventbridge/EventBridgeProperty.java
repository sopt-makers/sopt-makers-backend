package org.sopt.makers.clients.eventbridge;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "external.eventbridge")
public record EventBridgeProperty(@NotBlank String roleArn, @NotBlank String region) {}
