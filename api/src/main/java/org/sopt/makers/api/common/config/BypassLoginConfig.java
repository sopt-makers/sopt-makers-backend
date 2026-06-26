package org.sopt.makers.api.common.config;

import org.sopt.makers.domain.auth.port.BypassLoginPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BypassLoginConfig {

  @Bean
  public BypassLoginPort bypassLoginPort(AuthProperty authProperty) {
    AuthProperty.BypassLogin bypassLogin = authProperty.bypassLogin();
    return new BypassLoginPort() {
      @Override
      public String phone() {
        return bypassLogin.phone();
      }

      @Override
      public String code() {
        return bypassLogin.code();
      }

      @Override
      public String name() {
        return bypassLogin.name();
      }
    };
  }
}
