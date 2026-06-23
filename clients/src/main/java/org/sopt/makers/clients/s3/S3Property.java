package org.sopt.makers.clients.s3;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "external.s3")
public record S3Property(
    @NotBlank String bucket, @NotBlank String region, @NotBlank String baseDir) {}
