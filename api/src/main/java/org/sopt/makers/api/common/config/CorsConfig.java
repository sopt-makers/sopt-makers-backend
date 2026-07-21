package org.sopt.makers.api.common.config;

import static org.sopt.makers.api.common.security.SecurityConstant.API_KEY_HEADER;
import static org.sopt.makers.api.common.security.SecurityConstant.CORS_ALLOWED_ORIGINS;
import static org.sopt.makers.api.common.security.SecurityConstant.PATTERN_ALL;
import static org.sopt.makers.api.common.security.SecurityConstant.SERVICE_NAME_HEADER;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList(CORS_ALLOWED_ORIGINS));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    config.setAllowedHeaders(
        List.of("Authorization", "Content-Type", API_KEY_HEADER, SERVICE_NAME_HEADER));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(PATTERN_ALL, config);
    return source;
  }
}
