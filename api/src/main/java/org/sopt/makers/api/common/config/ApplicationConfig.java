package org.sopt.makers.api.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  SecurityProperty.class,
  // ApplicationProperty.class  → storage 모듈 구현 시 storage.config로 이동
  // ExternalProperty.class     → clients 모듈 구현 시 clients.config로 이동
})
public class ApplicationConfig {}
