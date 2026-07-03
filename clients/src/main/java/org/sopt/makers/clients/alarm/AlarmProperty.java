package org.sopt.makers.clients.alarm;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "external.alarm")
public record AlarmProperty(
    @NotBlank String url,
    @NotBlank String key,
    @NotBlank String arn,
    @NotBlank String headerService) {}
