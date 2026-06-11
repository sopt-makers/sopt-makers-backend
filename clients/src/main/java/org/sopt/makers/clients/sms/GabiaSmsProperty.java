package org.sopt.makers.clients.sms;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "external.sms.gabia")
public record GabiaSmsProperty(
    @NotBlank String smsId, @NotBlank String apiKey, @NotBlank String senderNumber) {}
