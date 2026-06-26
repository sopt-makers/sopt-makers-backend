package org.sopt.makers.api.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({
  AuthProperty.class,
  SecurityProperty.class,
})
public class ApplicationConfig {}
